package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.Expediente;

@Repository
public interface IExpedienteRepository extends JpaRepository<Expediente, Integer>
{
	
	List<Expediente> findByAdministradoCodigoOrderByFechaRegistroDesc(Integer codigo);
}