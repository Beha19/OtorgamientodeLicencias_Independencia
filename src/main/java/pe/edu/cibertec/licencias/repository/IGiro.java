package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Giro;

public interface IGiro extends JpaRepository<Giro, Integer>
{
    List<Giro> findByActivoTrueOrderByNombreAsc();
}