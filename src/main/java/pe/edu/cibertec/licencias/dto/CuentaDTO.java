package pe.edu.cibertec.licencias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CuentaDTO
{
	@NotBlank(message = "Ingrese el DNI")
	@Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
	private String dni;

	@NotBlank(message = "Ingrese los nombres")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String nombres;

	@NotBlank(message = "Ingrese los apellidos")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String apellidos;

	@NotBlank(message = "Ingrese el correo")
	@Email(message = "Correo no válido")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String correo;

	@Pattern(regexp = "\\d{9}|", message = "El teléfono debe tener 9 dígitos")
	private String telefono;

	@NotBlank(message = "Ingrese la contraseña")
	@Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
	private String clave;
}