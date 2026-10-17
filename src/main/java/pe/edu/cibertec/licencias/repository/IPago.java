package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Pago;

public interface IPago extends JpaRepository<Pago, Integer>
{
    List<Pago> findByExpediente_IdExpediente(Integer idExpediente);
}