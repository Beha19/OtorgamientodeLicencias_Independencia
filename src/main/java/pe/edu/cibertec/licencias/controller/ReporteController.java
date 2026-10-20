package pe.edu.cibertec.licencias.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.cibertec.licencias.service.ReporteService;

@RestController
public class ReporteController
{
	private final ReporteService reporteService;

	public ReporteController(ReporteService reporteService)
	{
		this.reporteService = reporteService;
	}

	@GetMapping("/subgerente/reportes/certificado/{idLicencia}")
	public ResponseEntity<byte[]> certificado(@PathVariable Integer idLicencia)
	{
		try
		{
			byte[] pdf = reporteService.certificadoLicencia(idLicencia);
			return respuestaPdf(pdf, "certificado-" + idLicencia + ".pdf");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/revisor/reportes/oficio/{idObservacion}")
	public ResponseEntity<byte[]> oficio(@PathVariable Integer idObservacion)
	{
		try
		{
			byte[] pdf = reporteService.oficioObservacion(idObservacion);
			return respuestaPdf(pdf, "oficio-" + idObservacion + ".pdf");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	private ResponseEntity<byte[]> respuestaPdf(byte[] pdf, String nombreArchivo)
	{
		HttpHeaders cabeceras = new HttpHeaders();
		cabeceras.setContentType(MediaType.APPLICATION_PDF);
		cabeceras.setContentDisposition(ContentDisposition.inline().filename(nombreArchivo).build());
		return ResponseEntity.ok().headers(cabeceras).body(pdf);
	}
}