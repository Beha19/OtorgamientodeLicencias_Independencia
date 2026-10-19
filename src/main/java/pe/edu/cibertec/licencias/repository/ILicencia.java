package pe.edu.cibertec.licencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Licencia;

public interface ILicencia extends JpaRepository<Licencia, Integer>
{
    Optional<Licencia> findByExpediente_IdExpediente(Integer idExpediente);

    Optional<Licencia> findByNumero(String numero);
}