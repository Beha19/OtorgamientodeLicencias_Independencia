package pe.edu.cibertec.licencias.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "persona_reniec")
@Data
public class PersonaReniec
{
    @Id                                        
    private String dni;

    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
}