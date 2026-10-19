package pe.edu.cibertec.licencias.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "feriado")
@Data
public class Feriado
{
    @Id                                        
    private LocalDate fecha;

    private String descripcion;
}