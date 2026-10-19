package pe.edu.cibertec.licencias.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "respuesta_condicion")
@Data
public class RespuestaCondicion {

    @EmbeddedId                               
    private RespuestaCondicionId id = new RespuestaCondicionId();

    @ManyToOne
    @MapsId("idDeclaracion")                   
    @JoinColumn(name = "id_declaracion")
    private DeclaracionJurada declaracion;

    @ManyToOne
    @MapsId("idCondicion")                     
    @JoinColumn(name = "id_condicion")
    private CondicionSeguridad condicion;

    private Boolean cumple;                    
}