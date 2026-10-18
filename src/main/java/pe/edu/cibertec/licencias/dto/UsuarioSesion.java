package pe.edu.cibertec.licencias.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UsuarioSesion implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String rol;
    
  
    public String getPrimerNombre()
    {
        return nombres == null || nombres.isBlank() ? "" : nombres.trim().split("\\s+")[0];
    }

    
    public String getNombreCorto()
    {
        String apellido = apellidos == null || apellidos.isBlank() ? "" : apellidos.trim().split("\\s+")[0];
        return (getPrimerNombre() + " " + apellido).trim();
    }

    public String getIniciales()
    {
        String n = getPrimerNombre();
        String a = apellidos == null || apellidos.isBlank() ? "" : apellidos.trim().split("\\s+")[0];
        String ini = (n.isEmpty() ? "" : n.substring(0, 1)) + (a.isEmpty() ? "" : a.substring(0, 1));
        return ini.toUpperCase();
    }
    
    public String getRutaInicio()
    {
        return "/" + rol.toLowerCase() + "/inicio";
    }
}