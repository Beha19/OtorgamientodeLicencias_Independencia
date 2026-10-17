package pe.edu.cibertec.licencias.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "expediente")
@Data
public class Expediente
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idExpediente;

    private String numero;                     

    @ManyToOne
    @JoinColumn(name = "id_solicitante")
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "id_establecimiento")
    private Establecimiento establecimiento;

    @ManyToOne
    @JoinColumn(name = "id_giro")
    private Giro giro;

    private String modalidad = "INDETERMINADA";    
    private LocalDate fechaFinVigencia;            
    private String tipoPersona = "NATURAL";        
    private String nombreComercial;
    private String actividad;
    private String autorizacionSectorial;
    private String ruc;                            
    private String razonSocial;
    private String estado = "BORRADOR";
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEnvio;
    private LocalDate fechaLimite;               

    @ManyToOne
    @JoinColumn(name = "id_revisor")
    private Usuario revisor;

    @ManyToOne
    @JoinColumn(name = "id_tecnico_asignado")
    private Usuario tecnicoAsignado;

    private LocalDateTime fechaAsignacion;

    @PrePersist
    public void alRegistrar()
    {
        if (fechaRegistro == null)
        {
            fechaRegistro = LocalDateTime.now();
        }
    }
}