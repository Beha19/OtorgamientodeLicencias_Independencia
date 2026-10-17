package pe.edu.cibertec.licencias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "giro")
public class Giro
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_giro")
	private Integer codigo;

	@Column(name = "codigo_ciiu")
	private String codigoCiiu;

	private String nombre;

	@Column(name = "nivel_riesgo")
	private String nivelRiesgo;

	private Boolean activo;
}