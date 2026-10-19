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
@Table(name = "documento_adjunto")
@Data
public class DocumentoAdjunto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDocumento;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    private String tipo;              
    private String nombreArchivo;
    private String ruta;
    private LocalDateTime fechaSubida;

    @PrePersist
    public void alSubir()
    {
        if (fechaSubida == null)
        {
            fechaSubida = LocalDateTime.now();
        }
    }
}
