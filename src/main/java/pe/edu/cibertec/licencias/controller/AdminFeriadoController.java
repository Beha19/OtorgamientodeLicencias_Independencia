package pe.edu.cibertec.licencias.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.edu.cibertec.licencias.dto.FeriadoDTO;
import pe.edu.cibertec.licencias.entity.Feriado;
import pe.edu.cibertec.licencias.service.FeriadoService;

@Controller
@RequestMapping("/admin/feriados")
public class AdminFeriadoController
{
	private final FeriadoService feriadoService;

	public AdminFeriadoController(FeriadoService feriadoService)
	{
		this.feriadoService = feriadoService;
	}

	@ModelAttribute("feriados")
	public List<Feriado> feriados()
	{
		return feriadoService.listar();
	}

	@GetMapping
	public String lista(Model model)
	{
		model.addAttribute("feriado", new FeriadoDTO());
		return "admin/feriados";
	}

	@GetMapping("/{fecha}/editar")
	public String editar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			Model model, RedirectAttributes redirect)
	{
		Optional<Feriado> encontrado = feriadoService.buscar(fecha);

		if (encontrado.isEmpty())
		{
			redirect.addFlashAttribute("error", "El feriado no existe.");
			return "redirect:/admin/feriados";
		}

		FeriadoDTO dto = new FeriadoDTO();
		dto.setFecha(encontrado.get().getFecha());
		dto.setDescripcion(encontrado.get().getDescripcion());
		dto.setEdicion(true);

		model.addAttribute("feriado", dto);
		return "admin/feriados";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("feriado") FeriadoDTO feriado,
			BindingResult result, RedirectAttributes redirect)
	{
		if (!feriado.isEdicion() && !result.hasFieldErrors("fecha") && feriadoService.existe(feriado.getFecha()))
		{
			result.rejectValue("fecha", "duplicado", "Ya existe un feriado en esa fecha");
		}

		if (result.hasErrors())
		{
			return "admin/feriados";
		}

		boolean esEdicion = feriado.isEdicion();
		feriadoService.guardar(feriado);
		redirect.addFlashAttribute("mensaje",
				esEdicion ? "Feriado actualizado correctamente." : "Feriado agregado correctamente.");
		return "redirect:/admin/feriados";
	}

	@PostMapping("/{fecha}/eliminar")
	public String eliminar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			RedirectAttributes redirect)
	{
		feriadoService.eliminar(fecha);
		redirect.addFlashAttribute("mensaje", "Feriado eliminado.");
		return "redirect:/admin/feriados";
	}
}