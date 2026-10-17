package pe.edu.cibertec.licencias.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "declaracion_jurada")
@Data
public class DeclaracionJurada
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDeclaracion;

    @OneToOne                                  
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    private Integer aforo;
    private Integer antiguedadAnios;
    private BigDecimal areaTechadaM2;
    private Boolean aceptaDj = false;
    private LocalDateTime fecha;

    @PrePersist
    public void alDeclarar()
    {
        if (fecha == null)
        {
            fecha = LocalDateTime.now();
        }
    }
}