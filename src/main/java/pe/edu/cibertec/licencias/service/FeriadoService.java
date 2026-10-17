package pe.edu.cibertec.licencias.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.dto.FeriadoDTO;
import pe.edu.cibertec.licencias.entity.Feriado;
import pe.edu.cibertec.licencias.repository.IFeriado;

@Service
public class FeriadoService
{
	private final IFeriado feriadoRepository;

	public FeriadoService(IFeriado feriadoRepository)
	{
		this.feriadoRepository = feriadoRepository;
	}

	public List<Feriado> listar()
	{
		return feriadoRepository.findAll(Sort.by("fecha"));
	}

	public Optional<Feriado> buscar(LocalDate fecha)
	{
		return feriadoRepository.findById(fecha);
	}

	public boolean existe(LocalDate fecha)
	{
		return feriadoRepository.existsById(fecha);
	}

	public Feriado guardar(FeriadoDTO dto)
	{
		Feriado feriado = new Feriado();
		feriado.setFecha(dto.getFecha());
		feriado.setDescripcion(dto.getDescripcion().trim());
		return feriadoRepository.save(feriado);
	}

	public void eliminar(LocalDate fecha)
	{
		feriadoRepository.deleteById(fecha);
	}
}