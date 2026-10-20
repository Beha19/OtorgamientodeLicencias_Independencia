package pe.edu.cibertec.licencias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.cibertec.licencias.entity.Observacion;

public interface IObservacion extends JpaRepository<Observacion, Integer>
{
    List<Observacion> findByExpediente_IdExpedienteOrderByFechaDesc(Integer idExpediente);
    @Query("SELECT MAX(o.numeroOficio) FROM Observacion o WHERE o.numeroOficio LIKE :prefijo")
    Optional<String> ultimoOficio(@Param("prefijo") String prefijo);
}