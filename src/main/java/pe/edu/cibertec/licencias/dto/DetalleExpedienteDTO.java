package pe.edu.cibertec.licencias.dto;

import java.util.List;

import pe.edu.cibertec.licencias.entity.DeclaracionJurada;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Licencia;
import pe.edu.cibertec.licencias.entity.Observacion;
import pe.edu.cibertec.licencias.entity.Pago;
import pe.edu.cibertec.licencias.entity.Representante;

public record DetalleExpedienteDTO(
		Expediente expediente,
		Representante representante,
		DeclaracionJurada declaracion,
		List<DocumentoAdjunto> documentos,
		List<Pago> pagos,
		List<Observacion> observaciones,
		Licencia licencia)
{
}