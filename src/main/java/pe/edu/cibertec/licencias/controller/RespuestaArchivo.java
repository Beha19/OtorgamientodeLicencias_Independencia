package pe.edu.cibertec.licencias.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;

import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;


// lezama
final class RespuestaArchivo
{
    private RespuestaArchivo()
    {
    }

    static ResponseEntity<Resource> enLinea(DocumentoAdjunto d, Resource archivo)
    {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(d.getNombreArchivo()).build().toString())
                .contentType(MediaTypeFactory.getMediaType(archivo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(archivo);
    }
}
