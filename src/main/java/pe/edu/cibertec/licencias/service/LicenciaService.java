package pe.edu.cibertec.licencias.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.entity.Giro;
import pe.edu.cibertec.licencias.entity.Licencia;
import pe.edu.cibertec.licencias.repository.IGiro;
import pe.edu.cibertec.licencias.repository.ILicencia;

@Service
public class LicenciaService
{
	private final ILicencia licenciaRepository;
	private final IGiro giroRepository;

	public LicenciaService(ILicencia licenciaRepository, IGiro giroRepository)
	{
		this.licenciaRepository = licenciaRepository;
		this.giroRepository = giroRepository;
	}

	public List<Giro> listarGiros()
	{
		return giroRepository.findByActivoTrueOrderByNombre();
	}

	public List<Licencia> buscar(LocalDate desde, LocalDate hasta, Integer giro)
	{
		return licenciaRepository.buscar(
				desde == null ? null : desde.atStartOfDay(),
				hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
				giro);
	}
}