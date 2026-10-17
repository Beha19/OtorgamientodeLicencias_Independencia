package pe.edu.cibertec.licencias.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "expediente")
public class Expediente
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_expediente")
	private Integer codigo;

	private String numero;

	@ManyToOne
	@JoinColumn(name = "id_administrado")
	private Usuario administrado;

	@ManyToOne
	@JoinColumn(name = "id_giro")
	private Giro giro;

	@Column(name = "nombre_comercial")
	private String nombreComercial;

	private String estado;

	@Column(name = "fecha_registro")
	private LocalDateTime fechaRegistro;

	@Column(name = "fecha_limite")
	private LocalDate fechaLimite;
}