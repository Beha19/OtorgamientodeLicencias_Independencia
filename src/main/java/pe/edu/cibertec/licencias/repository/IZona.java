package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Zona;

public interface IZona extends JpaRepository<Zona, Integer>
{
    List<Zona> findByActivoTrueOrderByNombreAsc();
}