package pe.edu.cibertec.licencias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.cibertec.licencias.entity.Expediente;

public interface IExpediente extends JpaRepository<Expediente, Integer>
{
    List<Expediente> findByEstadoOrderByFechaLimiteAsc(String estado);
    List<Expediente> findBySolicitante_IdUsuarioOrderByFechaRegistroDesc(Integer idUsuario);
    List<Expediente> findByTecnicoAsignado_IdUsuarioAndEstadoOrderByFechaLimiteAsc(Integer idTecnico, String estado);
    Optional<Expediente> findByNumero(String numero);
    boolean existsByEstablecimiento_IdEstablecimientoAndEstadoIn(Integer idEstablecimiento, List<String> estados);

    // Carga actual de un técnico (la usa AsignacionService para el reparto equitativo)
    long countByTecnicoAsignado_IdUsuarioAndEstado(Integer idUsuario, String estado);

    // Aprobados que todavía no tienen licencia (la usa FirmaService)
    @Query("SELECT e FROM Expediente e WHERE e.estado = 'APROBADO' "
         + "AND NOT EXISTS (SELECT l FROM Licencia l WHERE l.expediente = e) "
         + "ORDER BY e.fechaLimite ASC")
    List<Expediente> pendientesDeFirma();
    
    // El mayor número del año, para calcular el siguiente (ej. prefijo "2026-%")
    @Query("SELECT MAX(e.numero) FROM Expediente e WHERE e.numero LIKE :prefijo")
    Optional<String> ultimoNumero(@Param("prefijo") String prefijo);
    
    
    
    
    
}