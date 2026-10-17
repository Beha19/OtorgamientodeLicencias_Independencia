package pe.edu.cibertec.licencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Representante;

public interface IRepresentante extends JpaRepository<Representante, Integer>
{
    Optional<Representante> findByExpediente_IdExpediente(Integer idExpediente);
}