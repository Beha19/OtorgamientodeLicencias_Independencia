package pe.edu.cibertec.licencias.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "feriado")
public class Feriado
{
	@Id
	private LocalDate fecha;

	@Column(name = "descripcion")
	private String descripcion;
}