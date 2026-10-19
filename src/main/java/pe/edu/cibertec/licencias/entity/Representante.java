package pe.edu.cibertec.licencias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "representante")
@Data
public class Representante
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRepresentante;

    @OneToOne                                  
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    private String dni;
    private String nombres;
    private String apellidos;
    private String tipoPoder;                 
}