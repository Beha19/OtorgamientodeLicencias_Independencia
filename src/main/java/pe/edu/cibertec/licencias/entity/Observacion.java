package pe.edu.cibertec.licencias.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "observacion")
@Data
public class Observacion
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObservacion;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")           
    private Usuario usuario;

    private String etapa;                      
    private String numeroOficio;

    @Column(columnDefinition = "TEXT")         
    private String descripcion;

    private LocalDateTime fecha;
    private Boolean subsanada = false;

    @PrePersist
    public void alObservar()
    {
        if (fecha == null)
        {
            fecha = LocalDateTime.now();
        }
    }
}
