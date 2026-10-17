package pe.edu.cibertec.licencias.entity;

import java.math.BigDecimal;
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
@Table(name = "pago")
@Data
public class Pago
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    private String codigoOrden;
    private BigDecimal monto;
    private String medio = "TARJETA";          
    private String estado = "PENDIENTE";      
    private String referenciaPasarela;
    private LocalDateTime fecha;
    private String numeroRecibo;
    private LocalDateTime fechaConfirmacion;

    @PrePersist
    public void alCrear()
    {
        if (fecha == null)
        {
            fecha = LocalDateTime.now();
        }
    }
}
