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



@Service
@RequiredArgsConstructor
public class RevisionService
{
    private final IExpediente expedientes;
    private final IUsuario usuarios;
    private final AsignacionService asignacion;
    private final OficioService oficios;

    
    @Transactional(readOnly = true)
    public List<BandejaFila> bandeja()
    {
        return expedientes.findByEstadoOrderByFechaLimiteAsc("PENDIENTE_REVISION").stream()
                .map(BandejaFila::de)
                .toList();
    }

    public Expediente exigirPendiente(Integer idExpediente)
    {
        Expediente e = expedientes.findById(idExpediente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el expediente"));
        if (!"PENDIENTE_REVISION".equals(e.getEstado()))
        {
            throw new ReglaNegocioException("Este expediente ya no está pendiente de revisión");
        }
        return e;
    }

   
    @Transactional
    public Expediente verificar(Integer idExpediente, Integer idRevisor)
    {
        Expediente e = exigirPendiente(idExpediente);
        Usuario revisor = cargarUsuario(idRevisor);

        e.setRevisor(revisor);
        asignacion.asignarAutomatico(e, revisor);
        e.setEstado("EN_EVALUACION");
        return expedientes.save(e);
    }

    
    @Transactional
    public Expediente observar(Integer idExpediente, Integer idRevisor, String descripcion)
    {
        if (descripcion == null || descripcion.isBlank())
        {
            throw new ReglaNegocioException("Escribe la observación para el solicitante");
        }
        Expediente e = exigirPendiente(idExpediente);
        Usuario revisor = cargarUsuario(idRevisor);

        e.setRevisor(revisor);
        e.setEstado("OBSERVADO");
        e = expedientes.save(e);
        oficios.registrar(e, revisor, "REVISION", descripcion.trim());
        return e;
    }

    private Usuario cargarUsuario(Integer id)
    {
        return usuarios.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró al usuario de la sesión"));
    }
}
