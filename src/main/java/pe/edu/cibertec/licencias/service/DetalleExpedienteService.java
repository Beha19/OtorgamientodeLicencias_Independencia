package pe.edu.cibertec.licencias.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.DetalleExpediente;
import pe.edu.cibertec.licencias.entity.DeclaracionJurada;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.RespuestaCondicion;
import pe.edu.cibertec.licencias.repository.IDeclaracionJurada;
import pe.edu.cibertec.licencias.repository.IDocumentoAdjunto;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.IObservacion;
import pe.edu.cibertec.licencias.repository.IRepresentante;
import pe.edu.cibertec.licencias.repository.IRespuestaCondicion;
import pe.edu.cibertec.licencias.repository.IZona;


// ARMA LA PANTALLA DE DETALLE QUE USAN EL REVISOR Y EL TÉCNICO
@Service
@RequiredArgsConstructor
public class DetalleExpedienteService
{
    private final IExpediente expedientes;
    private final IRepresentante representantes;
    private final IDocumentoAdjunto documentos;
    private final IDeclaracionJurada declaraciones;
    private final IRespuestaCondicion respuestas;
    private final IObservacion observaciones;
    private final IZona zonas;

    @Transactional(readOnly = true)
    public DetalleExpediente cargar(Integer idExpediente)
    {
        Expediente e = expedientes.findById(idExpediente)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el expediente"));

        DeclaracionJurada dj = declaraciones.findByExpediente_IdExpediente(idExpediente).orElse(null);
        List<RespuestaCondicion> respuestasDj = dj == null ? List.of()
                : respuestas.findByDeclaracion_IdDeclaracion(dj.getIdDeclaracion()).stream()
                        .sorted(Comparator.comparing(r -> r.getCondicion().getIdCondicion()))
                        .toList();

        boolean permitido = zonas.contarCompatibilidad(
                e.getGiro().getIdGiro(), e.getEstablecimiento().getZona().getIdZona()) > 0;

        return new DetalleExpediente(
                e,
                representantes.findByExpediente_IdExpediente(idExpediente).orElse(null),
                documentos.findByExpediente_IdExpediente(idExpediente),
                dj,
                respuestasDj,
                observaciones.findByExpediente_IdExpedienteOrderByFechaDesc(idExpediente),
                permitido);
    }

    // DOCUMENTO DE ESE EXPEDIENTE (SI NO COINCIDEN, NO SE ENTREGA)
    @Transactional(readOnly = true)
    public DocumentoAdjunto buscarDocumento(Integer idDocumento, Integer idExpediente)
    {
        DocumentoAdjunto d = documentos.findById(idDocumento)
                .orElseThrow(() -> new ReglaNegocioException("Documento no encontrado."));
        if (!d.getExpediente().getIdExpediente().equals(idExpediente))
        {
            throw new ReglaNegocioException("Documento no encontrado.");
        }
        return d;
    }
}
