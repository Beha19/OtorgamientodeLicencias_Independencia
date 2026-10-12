package pe.edu.cibertec.licencias.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Integer codigo;

	private String dni;
    
	private String nombres;
	
	private String apellidos;
	
	private String correo;
	
	@Column(name = "password")
	private String contrasena;
	
	private String telefono;
	
	private String rol;
	
	private Boolean disponible;
	
	private LocalDateTime ultimaAsignacion;
	
	private Boolean activo;
}
