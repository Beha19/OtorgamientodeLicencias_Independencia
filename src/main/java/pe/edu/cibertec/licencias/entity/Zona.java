package pe.edu.cibertec.licencias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "zona")
@Data
public class Zona
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idZona;

    private String codigo;                     
    private String nombre;
    private String descripcion;
    private Boolean activo = true;
}