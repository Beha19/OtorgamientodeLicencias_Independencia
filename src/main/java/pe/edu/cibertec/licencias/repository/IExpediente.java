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

    // El mayor número del año, para calcular el siguiente (ej. prefijo "2026-%")
    @Query("SELECT MAX(e.numero) FROM Expediente e WHERE e.numero LIKE :prefijo")
    Optional<String> ultimoNumero(@Param("prefijo") String prefijo);
}