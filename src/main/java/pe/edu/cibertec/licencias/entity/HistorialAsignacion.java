package pe.edu.cibertec.licencias.entity;

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
@Table(name = "historial_asignacion")
@Data
public class HistorialAsignacion
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    @ManyToOne
    @JoinColumn(name = "id_tecnico")
    private Usuario tecnico;

    @ManyToOne
    @JoinColumn(name = "id_asignado_por")
    private Usuario asignadoPor;

    private String tipo;                       
    private String motivo;                     
    private LocalDateTime fecha;

    @PrePersist
    public void alAsignar()
    {
        if (fecha == null)
        {
            fecha = LocalDateTime.now();
        }
    }
}