package pe.edu.cibertec.licencias.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.Licencia;

@Repository
public interface ILicencia extends JpaRepository<Licencia, Integer>
{
	@Query("""
			SELECT l FROM Licencia l
			WHERE (:desde IS NULL OR l.fechaEmision >= :desde)
			  AND (:hasta IS NULL OR l.fechaEmision < :hasta)
			  AND (:giro IS NULL OR l.expediente.giro.codigo = :giro)
			ORDER BY l.fechaEmision DESC
			""")
	List<Licencia> buscar(@Param("desde") LocalDateTime desde,
			@Param("hasta") LocalDateTime hasta,
			@Param("giro") Integer giro);
}