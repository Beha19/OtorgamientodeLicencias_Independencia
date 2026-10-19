package pe.edu.cibertec.licencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Establecimiento;

public interface IEstablecimiento extends JpaRepository<Establecimiento, Integer>
{
    Optional<Establecimiento> findByViaTipoAndViaNombreAndNumeroAndInterior(
            String viaTipo, String viaNombre, String numero, String interior);
}