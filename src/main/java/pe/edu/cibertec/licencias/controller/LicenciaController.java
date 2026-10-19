package pe.edu.cibertec.licencias.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.cibertec.licencias.service.LicenciaService;

@Controller
@RequestMapping("/licencias")
public class LicenciaController
{
	private final LicenciaService licenciaService;

	public LicenciaController(LicenciaService licenciaService)
	{
		this.licenciaService = licenciaService;
	}

	@GetMapping
	public String lista(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam(required = false) Integer giro,
			Model model)
	{
		model.addAttribute("giros", licenciaService.listarGiros());
		model.addAttribute("desde", desde);
		model.addAttribute("hasta", hasta);
		model.addAttribute("giro", giro);

		if (desde != null && hasta != null && desde.isAfter(hasta))
		{
			model.addAttribute("error", "La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
			model.addAttribute("licencias", List.of());
			return "licencias/lista";
		}

		model.addAttribute("licencias", licenciaService.buscar(desde, hasta, giro));
		return "licencias/lista";
	}
}