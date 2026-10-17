package pe.edu.cibertec.licencias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.repository.IExpedienteRepository;

@Service
public class ExpedienteService
{
	private final IExpedienteRepository expedienteRepository;

	public ExpedienteService(IExpedienteRepository expedienteRepository)
	{
		this.expedienteRepository = expedienteRepository;
	}

	public List<Expediente> listarPorAdministrado(Integer codigoUsuario)
	{
		return expedienteRepository.findByAdministradoCodigoOrderByFechaRegistroDesc(codigoUsuario);
	}
}