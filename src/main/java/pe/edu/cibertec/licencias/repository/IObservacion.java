package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Observacion;

public interface IObservacion extends JpaRepository<Observacion, Integer>
{
    List<Observacion> findByExpediente_IdExpedienteOrderByFechaDesc(Integer idExpediente);
}