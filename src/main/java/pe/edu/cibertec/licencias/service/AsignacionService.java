package pe.edu.cibertec.licencias.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.HistorialAsignacion;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.IHistorialAsignacion;
import pe.edu.cibertec.licencias.repository.IUsuario;


// REPARTO EQUITATIVO: EL EXPEDIENTE VA AL TÉCNICO CON MENOS EXPEDIENTES EN EVALUACIÓN
@Service
@RequiredArgsConstructor
public class AsignacionService
{
    private final IUsuario usuarios;
    private final IExpediente expedientes;
    private final IHistorialAsignacion historial;

    // SOLO PONE AL TÉCNICO EN EL EXPEDIENTE Y GUARDA EL HISTORIAL; EL QUE LLAMA GUARDA EL EXPEDIENTE
    @Transactional
    public Usuario asignarAutomatico(Expediente e, Usuario asignadoPor)
    {
        List<Usuario> tecnicos = usuarios.findByRolAndActivoTrue("TECNICO").stream()
                .filter(t -> Boolean.TRUE.equals(t.getDisponible()))
                .toList();
        if (tecnicos.isEmpty())
        {
            throw new ReglaNegocioException("No hay técnicos disponibles para asignar el expediente");
        }

        // MENOS CARGA PRIMERO; SI EMPATAN, EL QUE HACE MÁS TIEMPO NO RECIBE NINGUNO
        Usuario elegido = tecnicos.stream()
                .min(Comparator
                        .comparingLong((Usuario t) -> expedientes
                                .countByTecnicoAsignado_IdUsuarioAndEstado(t.getIdUsuario(), "EN_EVALUACION"))
                        .thenComparing(Usuario::getUltimaAsignacion,
                                Comparator.nullsFirst(Comparator.naturalOrder())))
                .get();

        LocalDateTime ahora = LocalDateTime.now();
        e.setTecnicoAsignado(elegido);
        e.setFechaAsignacion(ahora);

        elegido.setUltimaAsignacion(ahora);
        usuarios.save(elegido);

        HistorialAsignacion h = new HistorialAsignacion();
        h.setExpediente(e);
        h.setTecnico(elegido);
        h.setAsignadoPor(asignadoPor);
        h.setTipo("AUTOMATICA");
        historial.save(h);

        return elegido;
    }
}
