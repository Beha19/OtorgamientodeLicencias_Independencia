package pe.edu.cibertec.licencias.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReporteService
{
	private final DataSource dataSource;

	
	private final Map<String, JasperReport> compilados = new ConcurrentHashMap<>();

	public ReporteService(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public byte[] certificadoLicencia(Integer idLicencia) throws JRException, SQLException, IOException
	{
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("ID_LICENCIA", idLicencia);
		return generarPdf("certificado_licencia", parametros);
	}

	public byte[] oficioObservacion(Integer idObservacion) throws JRException, SQLException, IOException
	{
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("ID_OBSERVACION", idObservacion);
		return generarPdf("oficio_observacion", parametros);
	}

	private byte[] generarPdf(String nombreReporte, Map<String, Object> parametros)
			throws JRException, SQLException, IOException
	{
		JasperReport reporte = cargar(nombreReporte);

		try (Connection conexion = dataSource.getConnection())
		{
			JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros, conexion);
			return JasperExportManager.exportReportToPdf(impresion);
		}
	}

	private JasperReport cargar(String nombreReporte) throws JRException, IOException
	{
		JasperReport reporte = compilados.get(nombreReporte);

		if (reporte == null)
		{
			try (InputStream archivo = new ClassPathResource("reports/" + nombreReporte + ".jrxml").getInputStream())
			{
				reporte = JasperCompileManager.compileReport(archivo);
			}
			compilados.put(nombreReporte, reporte);
		}
		return reporte;
	}
}