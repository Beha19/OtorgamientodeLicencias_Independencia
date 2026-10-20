package pe.edu.cibertec.licencias.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeriadoDTO
{
	@NotNull(message = "Seleccione la fecha")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fecha;

	@NotBlank(message = "Ingrese la descripción")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String descripcion;

	private boolean edicion;
}