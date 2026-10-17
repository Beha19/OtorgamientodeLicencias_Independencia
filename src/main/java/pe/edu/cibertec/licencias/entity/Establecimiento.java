package pe.edu.cibertec.licencias.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "establecimiento")
@Data
public class Establecimiento
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstablecimiento;

    private String viaTipo;                    
    private String viaNombre;
    private String numero;
    private String interior = "";              
    private String referencia;
    private BigDecimal areaM2;               
    private BigDecimal latitud;
    private BigDecimal longitud;

    @ManyToOne                                
    @JoinColumn(name = "id_zona")
    private Zona zona;
}