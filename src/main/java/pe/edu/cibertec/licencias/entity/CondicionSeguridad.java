package pe.edu.cibertec.licencias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "condicion_seguridad")
@Data
public class CondicionSeguridad
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCondicion;

    private String grupo;                     
    private String descripcion;
    private Boolean activo = true;
}