package pe.edu.cibertec.licencias.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "licencia")
@Data
public class Licencia
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLicencia;

    private String numero;

    @OneToOne                                  
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    @ManyToOne
    @JoinColumn(name = "id_firmante")          
    private Usuario firmante;

    private LocalDateTime fechaEmision;
    private String estado = "VIGENTE";         

    @PrePersist
    public void alEmitir()
    {
        if (fechaEmision == null)
        {
            fechaEmision = LocalDateTime.now();
        }
    }
}