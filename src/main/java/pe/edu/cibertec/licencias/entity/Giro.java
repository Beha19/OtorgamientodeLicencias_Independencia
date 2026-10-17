package pe.edu.cibertec.licencias.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "giro")
@Data
public class Giro
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGiro;

    private String codigoCiiu;
    private String nombre;
    private String nivelRiesgo;               
    private Boolean activo = true;


    @ManyToMany
    @JoinTable(name = "giro_zona",
               joinColumns = @JoinColumn(name = "id_giro"),
               inverseJoinColumns = @JoinColumn(name = "id_zona"))
    private Set<Zona> zonasPermitidas = new HashSet<>();
}