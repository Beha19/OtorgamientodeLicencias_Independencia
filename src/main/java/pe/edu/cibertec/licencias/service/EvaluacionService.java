package pe.edu.cibertec.licencias.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.BandejaFila;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.IUsuario;
import pe.edu.cibertec.licencias.repository.IZona;


// CU03: EL TÉCNICO EVALÚA SUS EXPEDIENTES (VALIDA GIRO-ZONA) Y LOS APRUEBA O DENIEGA
@Service
@RequiredArgsConstructor
public class EvaluacionService
{
    private final IExpediente expedientes;
    private final IUsuario usuarios;
    private final IZona zonas;
    private final OficioService oficios;

    @Transactional(readOnly = true)
    public List<BandejaFila> bandeja(Integer idTecnico)
    {
        return expedientes
                .findByTecnicoAsignado_IdUsuarioAndEstadoOrderByFechaLimiteAsc(idTecnico, "EN_EVALUACION")
                .stream()
                .map(BandejaFila::de)
                .toList();
    }

    // EL EXPEDIENTE DEBE SER DE ESTE TÉCNICO Y ESTAR EN EVALUACIÓN
    public Expediente cargarAsignado(Integer idExpediente, Integer idTecnico)
    {
        Expediente e = expedientes.findById(idExpediente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el expediente"));
        if (e.getTecnicoAsignado() == null || !e.getTecnicoAsignado().getIdUsuario().equals(idTecnico))
        {
            throw new ReglaNegocioException("Este expediente no está asignado a ti");
        }
        if (!"EN_EVALUACION".equals(e.getEstado()))
        {
            throw new ReglaNegocioException("Este expediente ya no está en evaluación");
        }
        return e;
    }

    @Transactional
    public Expediente aprobar(Integer idExpediente, Integer idTecnico)
    {
        Expediente e = cargarAsignado(idExpediente, idTecnico);

        // USO DE SUELO: EL GIRO DEBE ESTAR PERMITIDO EN LA ZONA DEL LOCAL (TABLA giro_zona)
        boolean permitido = zonas.contarCompatibilidad(
                e.getGiro().getIdGiro(), e.getEstablecimiento().getZona().getIdZona()) > 0;
        if (!permitido)
        {
            throw new ReglaNegocioException("No se puede aprobar: el giro \"" + e.getGiro().getNombre()
                    + "\" no está permitido en la zona \"" + e.getEstablecimiento().getZona().getNombre()
                    + "\". Si corresponde, deniega el expediente indicando el motivo.");
        }

        e.setEstado("APROBADO");
        return expedientes.save(e);
    }

    // EL MOTIVO QUEDA REGISTRADO COMO OBSERVACIÓN DE LA ETAPA EVALUACION (CON SU NÚMERO DE OFICIO)
    @Transactional
    public Expediente denegar(Integer idExpediente, Integer idTecnico, String motivo)
    {
        if (motivo == null || motivo.isBlank())
        {
            throw new ReglaNegocioException("Escribe el motivo de la denegación");
        }
        Expediente e = cargarAsignado(idExpediente, idTecnico);
        Usuario tecnico = usuarios.findById(idTecnico)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró al usuario de la sesión"));

        e.setEstado("DENEGADO");
        e = expedientes.save(e);
        oficios.registrar(e, tecnico, "EVALUACION", motivo.trim());
        return e;
    }
}
