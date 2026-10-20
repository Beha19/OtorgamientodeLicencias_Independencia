package pe.edu.cibertec.licencias.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pe.edu.cibertec.licencias.entity.DeclaracionJurada;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Observacion;
import pe.edu.cibertec.licencias.entity.Representante;
import pe.edu.cibertec.licencias.entity.RespuestaCondicion;


// TODO LO QUE EL REVISOR Y EL TÉCNICO NECESITAN VER DE UN EXPEDIENTE
@Getter
@AllArgsConstructor
public class DetalleExpediente
{
    private Expediente expediente;
    private Representante representante;              // null SI NO ACTÚA POR REPRESENTANTE
    private List<DocumentoAdjunto> documentos;
    private DeclaracionJurada declaracion;             // null SI NO HAY
    private List<RespuestaCondicion> respuestas;
    private List<Observacion> observaciones;
    private boolean usoDeSueloPermitido;               // ¿EL GIRO ESTÁ PERMITIDO EN LA ZONA DEL LOCAL?
}
