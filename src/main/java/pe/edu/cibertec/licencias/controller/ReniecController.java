package pe.edu.cibertec.licencias.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.cibertec.licencias.dto.ReniecDTO;
import pe.edu.cibertec.licencias.repository.IPersonaReniecRepository;

@RestController
@RequestMapping("/api/reniec")
public class ReniecController
{
	private final IPersonaReniecRepository personaReniecRepository;

	public ReniecController(IPersonaReniecRepository personaReniecRepository)
	{
		this.personaReniecRepository = personaReniecRepository;
	}

	@GetMapping("/{dni}")
	public ResponseEntity<ReniecDTO> buscar(@PathVariable String dni)
	{
		if (!dni.matches("\\d{8}"))
		{
			return ResponseEntity.badRequest().build();
		}

		return personaReniecRepository.findById(dni)
				.map(p -> ResponseEntity.ok(new ReniecDTO(p.getNombres(), p.getApellidos())))
				.orElse(ResponseEntity.notFound().build());
	}
}