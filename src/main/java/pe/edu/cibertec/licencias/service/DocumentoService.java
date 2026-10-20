package pe.edu.cibertec.licencias.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.repository.IDocumentoAdjunto;


@Service
@RequiredArgsConstructor
public class DocumentoService
{
    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> EXTENSIONES = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> TIPOS = Set.of("COPIA_DNI_CE", "CARTA_PODER_LEGALIZADA", "VIGENCIA_PODER");

    private final ExpedienteService expedientes;
    private final IDocumentoAdjunto documentos;

    @Value("${app.uploads.dir:uploads}")
    private String carpetaBase;

    @Transactional
    public void subir(Integer idExpediente, Integer idSolicitante, String tipo, MultipartFile archivo)
    {
        Expediente e = expedientes.cargarPropio(idExpediente, idSolicitante);

        String estado = String.valueOf(e.getEstado());
        if (!estado.equals("BORRADOR") && !estado.equals("PAGADO_SIN_ENVIO"))
        {
            throw new ReglaNegocioException("Ya no puedes adjuntar documentos: el trámite fue enviado.");
        }
        if (!TIPOS.contains(tipo))
        {
            throw new ReglaNegocioException("Tipo de documento no válido.");
        }
        if (archivo == null || archivo.isEmpty())
        {
            throw new ReglaNegocioException("Elige un archivo para adjuntar.");
        }
        if (archivo.getSize() > MAX_BYTES)
        {
            throw new ReglaNegocioException("El archivo supera los 5 MB permitidos.");
        }

        String original = StringUtils.cleanPath(Objects.requireNonNullElse(archivo.getOriginalFilename(), "documento"));
        int punto = original.lastIndexOf('.');
        String ext = punto >= 0 ? original.substring(punto + 1).toLowerCase() : "";
        if (!EXTENSIONES.contains(ext))
        {
            throw new ReglaNegocioException("Solo se aceptan archivos PDF, JPG o PNG.");
        }

        // NOMBRE PROPIO GUARDADO: NO SE USA EL NOMBRE DE USAURIO COMO ESENCIA
        String carpeta = "expediente-" + idExpediente;
        String guardado = tipo + "-" + UUID.randomUUID() + "." + ext;
        Path base = Path.of(carpetaBase).toAbsolutePath().normalize();
        try
        {
            Files.createDirectories(base.resolve(carpeta));
            Files.copy(archivo.getInputStream(), base.resolve(carpeta).resolve(guardado),
                       StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex)
        {
            throw new ReglaNegocioException("No se pudo guardar el archivo. Inténtalo de nuevo.");
        }

        DocumentoAdjunto d = new DocumentoAdjunto();
        d.setExpediente(e);
        d.setTipo(tipo);
        d.setNombreArchivo(original);
        d.setRuta(carpeta + "/" + guardado);   // RUTA RELATIVA: FUNCIONA EN PCD DE C/INTEGRANTE
        documentos.save(d);
    }

    // DEVUELVE EL DOCUMENTO SOLO SI EL EXPEDIENTE ES DEL SOLICITANTE Y EL DOCUMENTO ES DE ESE EXPEDIENTE
    @Transactional(readOnly = true)
    public DocumentoAdjunto buscar(Integer idDocumento, Integer idExpediente, Integer idSolicitante)
    {
        expedientes.cargarPropio(idExpediente, idSolicitante);
        DocumentoAdjunto d = documentos.findById(idDocumento)
                .orElseThrow(() -> new ReglaNegocioException("Documento no encontrado."));
        if (!d.getExpediente().getIdExpediente().equals(idExpediente))
        {
            throw new ReglaNegocioException("Documento no encontrado.");
        }
        return d;
    }

    public Resource recurso(DocumentoAdjunto d)
    {
        Path base = Path.of(carpetaBase).toAbsolutePath().normalize();
        Path archivo = base.resolve(d.getRuta()).normalize();
        if (!archivo.startsWith(base) || !Files.exists(archivo))   // EVITA SALIRSE DE LA CARPETA UPLOADS
        {
            throw new ReglaNegocioException("El archivo ya no está disponible.");
        }
        return new FileSystemResource(archivo);
    }
}