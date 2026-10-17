package pe.edu.cibertec.licencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.PersonaReniec;

@Repository
public interface IPersonaReniecRepository extends JpaRepository<PersonaReniec, String>
{

}