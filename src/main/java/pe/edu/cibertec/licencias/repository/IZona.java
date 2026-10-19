package pe.edu.cibertec.licencias.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.cibertec.licencias.entity.Zona;

public interface IZona extends JpaRepository<Zona, Integer>
{
    List<Zona> findByActivoTrueOrderByNombreAsc();

    // SIG: ZONA CON POLIGONO MARCADO EN EL MAPA
    @Query(value = "SELECT z.id_zona, z.codigo, z.nombre, z.descripcion, z.activo FROM zona z "
                 + "WHERE z.activo = TRUE AND ST_Contains(z.poligono, "
                 + "ST_GeomFromText(CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326, 'axis-order=long-lat')) "
                 + "LIMIT 1",
           nativeQuery = true)
    Optional<Zona> buscarPorPunto(@Param("lat") BigDecimal lat, @Param("lng") BigDecimal lng);
}