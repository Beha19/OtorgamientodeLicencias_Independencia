package pe.edu.cibertec.licencias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.repository.IExpediente;

@Service
public class ExpedienteService
{
	private final IExpediente expedienteRepository;

	public ExpedienteService(IExpediente expedienteRepository)
	{
		this.expedienteRepository = expedienteRepository;
	}

	public List<Expediente> listarPorSolicitante(Integer idUsuario)
	{
		return expedienteRepository.findBySolicitante_IdUsuarioOrderByFechaRegistroDesc(idUsuario);
	}
}