package pe.edu.cibertec.licencias.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Observacion;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IObservacion;


// REGISTRA UNA OBSERVACIÓN CON SU NÚMERO DE OFICIO (OF-2026-0001). ES LA BASE DEL OFICIO EN PDF
@Service
@RequiredArgsConstructor
public class OficioService
{
    private final IObservacion observaciones;

    @Transactional
    public Observacion registrar(Expediente e, Usuario autor, String etapa, String descripcion)
    {
        Observacion o = new Observacion();
        o.setExpediente(e);
        o.setUsuario(autor);
        o.setEtapa(etapa);                         // REVISION | EVALUACION
        o.setDescripcion(descripcion);
        o.setNumeroOficio(generarNumero(LocalDate.now().getYear()));
        return observaciones.save(o);
    }

    private String generarNumero(int anio)
    {
        String prefijo = "OF-" + anio + "-";
        int siguiente = observaciones.ultimoOficio(prefijo + "%")
                .map(n -> Integer.parseInt(n.substring(prefijo.length())) + 1)
                .orElse(1);
        return String.format("OF-%d-%04d", anio, siguiente);
    }
}
