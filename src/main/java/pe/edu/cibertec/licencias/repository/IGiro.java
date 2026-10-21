
package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Giro;
import org.springframework.data.jpa.repository.Query;

public interface IGiro extends JpaRepository<Giro, Integer> {

    List<Giro> findByActivoTrueOrderByNombreAsc();

    @Query("SELECT DISTINCT g FROM Giro g LEFT JOIN FETCH g.zonasPermitidas")
    List<Giro> findAllWithZonas();
}
