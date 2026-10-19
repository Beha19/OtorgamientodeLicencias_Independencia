package pe.edu.cibertec.licencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.DeclaracionJurada;

public interface IDeclaracionJurada extends JpaRepository<DeclaracionJurada, Integer>
{
    Optional<DeclaracionJurada> findByExpediente_IdExpediente(Integer idExpediente);
}