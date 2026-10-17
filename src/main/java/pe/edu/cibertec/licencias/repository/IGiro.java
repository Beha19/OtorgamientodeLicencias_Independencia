package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.Giro;

@Repository
public interface IGiro extends JpaRepository<Giro, Integer>
{
	List<Giro> findByActivoTrueOrderByNombre();
}