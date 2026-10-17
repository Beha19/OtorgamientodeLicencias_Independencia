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
@Table(name = "usuario")
@Data
public class Usuario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;                 
   
    private String dni;
    private String nombres;
    private String apellidos;
    private String correo;

    @Column(name = "password")                 
    private String clave;                     
    
    private String telefono;
    private String rol;                        
    private Boolean disponible = true;         
    private LocalDateTime ultimaAsignacion;    
    private Boolean activo = true;   
}