package pe.edu.cibertec.licencias.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Licencia;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.ILicencia;
import pe.edu.cibertec.licencias.repository.IUsuario;


// FIRMA DEL SUB GERENTE: DE EXPEDIENTE APROBADO A LICENCIA EMITIDA
@Service
@RequiredArgsConstructor
public class FirmaService
{
    private final IExpediente expedientes;
    private final ILicencia licencias;
    private final IUsuario usuarios;

    // APROBADOS QUE TODAVÍA NO TIENEN LICENCIA
    @Transactional(readOnly = true)
    public List<Expediente> pendientesDeFirma()
    {
        return expedientes.pendientesDeFirma();
    }

    @Transactional
    public Licencia firmar(Integer idExpediente, Integer idSubgerente)
    {
        Expediente e = expedientes.findById(idExpediente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el expediente"));
        if (!"APROBADO".equals(e.getEstado()))
        {
            throw new ReglaNegocioException("Solo se firman expedientes aprobados");
        }
        if (licencias.findByExpediente_IdExpediente(idExpediente).isPresent())
        {
            throw new ReglaNegocioException("Este expediente ya tiene su licencia emitida");
        }
        Usuario firmante = usuarios.findById(idSubgerente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró al usuario de la sesión"));

        Licencia l = new Licencia();
        l.setExpediente(e);
        l.setFirmante(firmante);
        l.setNumero(generarNumero(LocalDate.now().getYear()));
        l.setEstado("VIGENTE");
        return licencias.save(l);
    }

    private String generarNumero(int anio)
    {
        String prefijo = "LIC-" + anio + "-";
        int siguiente = licencias.ultimoNumero(prefijo + "%")
                .map(n -> Integer.parseInt(n.substring(prefijo.length())) + 1)
                .orElse(1);
        return String.format("LIC-%d-%04d", anio, siguiente);
    }
}
