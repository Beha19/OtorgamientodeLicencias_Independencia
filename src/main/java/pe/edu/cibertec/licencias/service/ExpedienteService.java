package pe.edu.cibertec.licencias.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.SolicitudForm;
import pe.edu.cibertec.licencias.entity.*;
import pe.edu.cibertec.licencias.repository.*;


@Service
@RequiredArgsConstructor
public class ExpedienteService
{
    private static final List<String> ESTADOS_ACTIVOS = List.of(   // ESTADO EN BASE DE DATOS = REGLA "SOLO 1 EXPEDIENTE ACTIVO POR LOCAL"
            "BORRADOR", "PAGADO_SIN_ENVIO", "PENDIENTE_REVISION",
            "VERIFICADO", "OBSERVADO", "EN_EVALUACION");

    private static final int PLAZO_DIAS_HABILES = 15;
    private static final BigDecimal DERECHO_TRAMITE = new BigDecimal("120.50");
    private static final List<String> MEDIOS_PAGO = List.of("TARJETA", "BILLETERA", "CAJA");

    private final IUsuario usuarios;
    private final IGiro giros;
    private final IZona zonas;
    private final IEstablecimiento establecimientos;
    private final IExpediente expedientes;
    private final IRepresentante representantes;
    private final IDeclaracionJurada declaraciones;
    private final ICondicionSeguridad condiciones;
    private final IRespuestaCondicion respuestas;
    private final IPago pagos;
    private final CalendarioService calendario;
    private final IDocumentoAdjunto documentos;

    @Transactional
    public Expediente guardarBorrador(SolicitudForm f, Integer idSolicitante)
    {
        Usuario solicitante = usuarios.findById(idSolicitante)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró al solicitante"));

        Giro giro = giros.findById(f.getIdGiro())
                .filter(g -> Boolean.TRUE.equals(g.getActivo()))
                .orElseThrow(() -> new ReglaNegocioException("El giro elegido no está disponible"));

        // SIG: ZONA EN EL SISTEMA CON PIN EN MAPA
        Zona zona = zonas.buscarPorPunto(f.getLatitud(), f.getLongitud())
                .orElseThrow(() -> new ReglaNegocioException(
                        "El punto marcado no está dentro de ninguna zona registrada del distrito"));

        // USO DE SUELO: EL GIRO DEBE ESTAR PERMITIDO EN LA ZONA DEL PIN (TABLA giro_zona)
        if (zonas.contarCompatibilidad(giro.getIdGiro(), zona.getIdZona()) == 0)
        {
            throw new ReglaNegocioException("El giro \"" + giro.getNombre()
                    + "\" no está permitido en la zona \"" + zona.getNombre()
                    + "\". Elige otro giro o ubica el pin en una zona compatible.");
        }

        validarPersonaYRepresentante(f);
        validarVigencia(f);

        // ESTABLECIMIENTO: SE REUTILIZA SI LA DIRECCIÓN YA EXISTE, SI NO SE CREA
        String interior = f.getInterior() == null ? "" : f.getInterior().trim();
        Establecimiento local = establecimientos
                .findByViaTipoAndViaNombreAndNumeroAndInterior(
                        f.getViaTipo(), f.getViaNombre().trim(), f.getNumero().trim(), interior)
                .orElseGet(Establecimiento::new);

        if (local.getIdEstablecimiento() != null
                && expedientes.existsByEstablecimiento_IdEstablecimientoAndEstadoIn(
                        local.getIdEstablecimiento(), ESTADOS_ACTIVOS))
        {
            throw new ReglaNegocioException("Ya existe un trámite activo para este local");
        }

        local.setViaTipo(f.getViaTipo());
        local.setViaNombre(f.getViaNombre().trim());
        local.setNumero(f.getNumero().trim());
        local.setInterior(interior);
        local.setReferencia(f.getReferencia());
        local.setAreaM2(f.getAreaM2());
        local.setLatitud(f.getLatitud());
        local.setLongitud(f.getLongitud());
        local.setZona(zona);
        local = establecimientos.save(local);

        // EXPEDIENTE ESTADO BORRADOR (SIN NÚMERO NI PLAZO: ESO SE ASIGNA AL ENVIAR)
        Expediente e = new Expediente();
        e.setSolicitante(solicitante);
        e.setEstablecimiento(local);
        e.setGiro(giro);
        e.setModalidad(f.getModalidad());
        if ("TEMPORAL".equals(f.getModalidad()))
        {
            e.setFechaFinVigencia(f.getFechaFinVigencia());
        }
        e.setTipoPersona(f.getTipoPersona());
        if ("JURIDICA".equals(f.getTipoPersona()))
        {
            e.setRuc(f.getRuc());
            e.setRazonSocial(f.getRazonSocial().trim());
        }
        e.setNombreComercial(f.getNombreComercial().trim());
        e.setActividad(f.getActividad());
        e.setAutorizacionSectorial(f.getAutorizacionSectorial());
        e.setEstado("BORRADOR");
        e = expedientes.save(e);

        // REPRESENTANTE (SOLO SI ACTÚA A TRAVÉS DE UNO)
        if (f.isConRepresentante())
        {
            Representante r = new Representante();
            r.setExpediente(e);
            r.setDni(f.getRepDni());
            r.setNombres(f.getRepNombres().trim());
            r.setApellidos(f.getRepApellidos().trim());
            // EL PODER LO DECIDE EL MAPRO SEGÚN EL TIPO DE PERSONA
            r.setTipoPoder("NATURAL".equals(f.getTipoPersona())
                    ? "CARTA_PODER_LEGALIZADA" : "VIGENCIA_PODER");
            representantes.save(r);
        }

        // DECLARACIÓN JURADA DE CONDICIONES DE SEGURIDAD
        DeclaracionJurada dj = new DeclaracionJurada();
        dj.setExpediente(e);
        dj.setAforo(f.getAforo());
        dj.setAntiguedadAnios(f.getAntiguedadAnios());
        dj.setAreaTechadaM2(f.getAreaTechadaM2());
        dj.setAceptaDj(f.isAceptaDj());
        dj = declaraciones.save(dj);

        // CONDICIÓN ACTIVA: MARCADA = CUMPLE / SIN MARCAR = NO CORRESPONDE
        for (CondicionSeguridad c : condiciones.findByActivoTrueOrderByGrupoAscIdCondicionAsc())
        {
            RespuestaCondicion rc = new RespuestaCondicion();
            rc.setId(new RespuestaCondicionId(dj.getIdDeclaracion(), c.getIdCondicion()));
            rc.setDeclaracion(dj);
            rc.setCondicion(c);
            rc.setCumple(Boolean.TRUE.equals(f.getRespuestas().get(c.getIdCondicion())));
            respuestas.save(rc);
        }

        return e;
    }

    // PAGO SIMULANDO DERECHO DE TRÁMITE: BORRADOR -> PAGADO_SIN_ENVIO
    @Transactional
    public Pago pagar(Integer idExpediente, Integer idSolicitante, String medio)
    {
        Expediente e = cargarPropio(idExpediente, idSolicitante);
        if (!"BORRADOR".equals(e.getEstado()))
        {
            throw new ReglaNegocioException("Este expediente ya no está en borrador");
        }
        if (!MEDIOS_PAGO.contains(medio))
        {
            throw new ReglaNegocioException("Elige un medio de pago válido");
        }

        int anio = LocalDate.now().getYear();
        Pago p = new Pago();
        p.setExpediente(e);
        p.setMonto(DERECHO_TRAMITE);
        p.setMedio(medio);
        p.setEstado("APROBADO");
        p.setCodigoOrden(String.format("ORD-%d-%04d", anio, e.getIdExpediente()));
        p.setNumeroRecibo(String.format("REC-%d-%04d", anio, e.getIdExpediente()));
        p.setReferenciaPasarela("SIMULADO");
        p.setFechaConfirmacion(LocalDateTime.now());
        p = pagos.save(p);

        e.setEstado("PAGADO_SIN_ENVIO");
        expedientes.save(e);
        return p;
    }

    // ENVÍO: ASIGNA NÚMERO Y PLAZO. PAGADO_SIN_ENVIO -> PENDIENTE_REVISION
    @Transactional
    public Expediente enviar(Integer idExpediente, Integer idSolicitante)
    {
        Expediente e = cargarPropio(idExpediente, idSolicitante);
        if (!"PAGADO_SIN_ENVIO".equals(e.getEstado()))
        {
            throw new ReglaNegocioException("Primero debes pagar el derecho de trámite");
        }

        // DOCUMENTOS: EL SISTEMA SOLO COMPRUEBA QUE EXISTAN; EL REVISOR VERIFICA QUE SEAN LEGALES Y CORRECTOS
        List<DocumentoAdjunto> adjuntos = documentos.findByExpediente_IdExpediente(idExpediente);

        boolean tieneDni = adjuntos.stream().anyMatch(d -> "COPIA_DNI_CE".equals(d.getTipo()));
        if (!tieneDni)
        {
            throw new ReglaNegocioException("Debes adjuntar la copia de tu DNI antes de enviar el trámite");
        }

        // SI ACTÚA POR REPRESENTANTE, TAMBIÉN DEBE ADJUNTAR EL PODER QUE LE CORRESPONDE
        Optional<Representante> representante = representantes.findByExpediente_IdExpediente(idExpediente);
        if (representante.isPresent())
        {
            String poder = representante.get().getTipoPoder();
            boolean tienePoder = adjuntos.stream().anyMatch(d -> poder.equals(d.getTipo()));
            if (!tienePoder)
            {
                String nombre = "CARTA_PODER_LEGALIZADA".equals(poder)
                        ? "la carta poder legalizada" : "la vigencia de poder";
                throw new ReglaNegocioException("Como actúas mediante representante, debes adjuntar " + nombre);
            }
        }

        boolean djAceptada = declaraciones.findByExpediente_IdExpediente(idExpediente)
                .map(d -> Boolean.TRUE.equals(d.getAceptaDj()))
                .orElse(false);
        if (!djAceptada)
        {
            throw new ReglaNegocioException("Debes aceptar la declaración jurada para enviar");
        }

        LocalDateTime ahora = LocalDateTime.now();
        e.setNumero(generarNumero(ahora.getYear()));
        e.setFechaEnvio(ahora);
        e.setFechaLimite(calendario.sumarDiasHabiles(ahora.toLocalDate(), PLAZO_DIAS_HABILES));
        e.setEstado("PENDIENTE_REVISION");
        return expedientes.save(e);
    }

    // PÚBLICO: TAMBIÉN LO USA DocumentoService PARA VERIFICAR QUE EL EXPEDIENTE ES DEL SOLICITANTE
    public Expediente cargarPropio(Integer idExpediente, Integer idSolicitante)
    {
        Expediente e = expedientes.findById(idExpediente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el expediente"));
        if (!e.getSolicitante().getIdUsuario().equals(idSolicitante))
        {
            throw new ReglaNegocioException("Este expediente no te pertenece");
        }
        return e;
    }

    private String generarNumero(int anio)
    {
        int siguiente = expedientes.ultimoNumero(anio + "-%")
                .map(n -> Integer.parseInt(n.substring(5)) + 1)
                .orElse(1);
        return String.format("%d-%06d", anio, siguiente);
    }

    private void validarPersonaYRepresentante(SolicitudForm f)
    {
        if ("JURIDICA".equals(f.getTipoPersona()))
        {
            if (f.getRuc() == null || !f.getRuc().matches("\\d{11}"))
            {
                throw new ReglaNegocioException("El RUC debe tener 11 dígitos");
            }
            if (f.getRazonSocial() == null || f.getRazonSocial().isBlank())
            {
                throw new ReglaNegocioException("Escribe la razón social");
            }
            if (!f.isConRepresentante())
            {
                throw new ReglaNegocioException("Una persona jurídica debe indicar a su representante legal");
            }
        }
        if (f.isConRepresentante())
        {
            if (f.getRepDni() == null || !f.getRepDni().matches("\\d{8}"))
            {
                throw new ReglaNegocioException("El DNI del representante debe tener 8 dígitos");
            }
            if (f.getRepNombres() == null || f.getRepNombres().isBlank()
                    || f.getRepApellidos() == null || f.getRepApellidos().isBlank())
            {
                throw new ReglaNegocioException("Escribe los nombres y apellidos del representante");
            }
        }
    }

    private void validarVigencia(SolicitudForm f)
    {
        if ("TEMPORAL".equals(f.getModalidad())
                && (f.getFechaFinVigencia() == null || !f.getFechaFinVigencia().isAfter(LocalDate.now())))
        {
            throw new ReglaNegocioException("Indica una fecha de fin de vigencia posterior a hoy");
        }
    }
    public List<Expediente> listarPorSolicitante(Integer idUsuario)
    {
        return expedientes.findBySolicitante_IdUsuarioOrderByFechaRegistroDesc(idUsuario);
    }
}