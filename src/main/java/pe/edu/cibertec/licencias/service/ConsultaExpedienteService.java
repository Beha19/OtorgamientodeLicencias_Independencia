package pe.edu.cibertec.licencias.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.dto.DetalleExpedienteDTO;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.repository.IDeclaracionJurada;
import pe.edu.cibertec.licencias.repository.IDocumentoAdjunto;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.ILicencia;
import pe.edu.cibertec.licencias.repository.IObservacion;
import pe.edu.cibertec.licencias.repository.IPago;
import pe.edu.cibertec.licencias.repository.IRepresentante;

@Service
public class ConsultaExpedienteService
{
	private final IExpediente expedienteRepository;
	private final IRepresentante representanteRepository;
	private final IDeclaracionJurada declaracionRepository;
	private final IDocumentoAdjunto documentoRepository;
	private final IPago pagoRepository;
	private final IObservacion observacionRepository;
	private final ILicencia licenciaRepository;

	public ConsultaExpedienteService(IExpediente expedienteRepository,
			IRepresentante representanteRepository,
			IDeclaracionJurada declaracionRepository,
			IDocumentoAdjunto documentoRepository,
			IPago pagoRepository,
			IObservacion observacionRepository,
			ILicencia licenciaRepository)
	{
		this.expedienteRepository = expedienteRepository;
		this.representanteRepository = representanteRepository;
		this.declaracionRepository = declaracionRepository;
		this.documentoRepository = documentoRepository;
		this.pagoRepository = pagoRepository;
		this.observacionRepository = observacionRepository;
		this.licenciaRepository = licenciaRepository;
	}

	public Optional<DetalleExpedienteDTO> buscar(Integer idExpediente, Integer idUsuario)
	{
		Optional<Expediente> encontrado = expedienteRepository.findById(idExpediente);

		if (encontrado.isEmpty())
		{
			return Optional.empty();
		}

		Expediente expediente = encontrado.get();

		if (expediente.getSolicitante() == null
				|| !idUsuario.equals(expediente.getSolicitante().getIdUsuario()))
		{
			return Optional.empty();
		}

		return Optional.of(new DetalleExpedienteDTO(
				expediente,
				representanteRepository.findByExpediente_IdExpediente(idExpediente).orElse(null),
				declaracionRepository.findByExpediente_IdExpediente(idExpediente).orElse(null),
				documentoRepository.findByExpediente_IdExpediente(idExpediente),
				pagoRepository.findByExpediente_IdExpediente(idExpediente),
				observacionRepository.findByExpediente_IdExpedienteOrderByFechaDesc(idExpediente),
				licenciaRepository.findByExpediente_IdExpediente(idExpediente).orElse(null)));
	}
}