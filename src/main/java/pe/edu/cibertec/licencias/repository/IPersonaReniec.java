package pe.edu.cibertec.licencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.PersonaReniec;

public interface IPersonaReniec extends JpaRepository<PersonaReniec, String>
{
}