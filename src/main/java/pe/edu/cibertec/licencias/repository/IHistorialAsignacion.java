package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.HistorialAsignacion;

public interface IHistorialAsignacion extends JpaRepository<HistorialAsignacion, Integer>
{
    List<HistorialAsignacion> findByExpediente_IdExpedienteOrderByFechaDesc(Integer idExpediente);
}