package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.RespuestaCondicion;
import pe.edu.cibertec.licencias.entity.RespuestaCondicionId;

public interface IRespuestaCondicion extends JpaRepository<RespuestaCondicion, RespuestaCondicionId>
{
    List<RespuestaCondicion> findByDeclaracion_IdDeclaracion(Integer idDeclaracion);
}