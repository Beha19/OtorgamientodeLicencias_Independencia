package pe.edu.cibertec.licencias.entity;

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
@Table(name = "licencia")
public class Licencia
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_licencia")
	private Integer codigo;

	private String numero;

	@ManyToOne
	@JoinColumn(name = "id_expediente")
	private Expediente expediente;

	@ManyToOne
	@JoinColumn(name = "id_firmante")
	private Usuario firmante;

	@Column(name = "fecha_emision")
	private LocalDateTime fechaEmision;

	private String estado;
}