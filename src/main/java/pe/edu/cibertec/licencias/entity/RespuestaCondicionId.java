package pe.edu.cibertec.licencias.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaCondicionId implements Serializable
{
    private Integer idDeclaracion;
    private Integer idCondicion;
}