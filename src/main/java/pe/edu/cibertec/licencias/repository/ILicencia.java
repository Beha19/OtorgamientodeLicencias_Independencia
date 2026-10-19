package pe.edu.cibertec.licencias.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.cibertec.licencias.entity.Licencia;

public interface ILicencia extends JpaRepository<Licencia, Integer>
{
    Optional<Licencia> findByExpediente_IdExpediente(Integer idExpediente);

    Optional<Licencia> findByNumero(String numero);

    @Query("""
            SELECT l FROM Licencia l
            WHERE (:desde IS NULL OR l.fechaEmision >= :desde)
              AND (:hasta IS NULL OR l.fechaEmision < :hasta)
              AND (:giro IS NULL OR l.expediente.giro.idGiro = :giro)
            ORDER BY l.fechaEmision DESC
            """)
    List<Licencia> buscar(@Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("giro") Integer giro);
}