package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.CondicionSeguridad;

public interface ICondicionSeguridad extends JpaRepository<CondicionSeguridad, Integer>
{
    List<CondicionSeguridad> findByActivoTrueOrderByGrupoAscIdCondicionAsc();
}