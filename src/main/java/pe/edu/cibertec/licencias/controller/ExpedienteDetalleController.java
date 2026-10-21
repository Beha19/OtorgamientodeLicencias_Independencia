package pe.edu.cibertec.licencias.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.licencias.dto.DetalleExpedienteDTO;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.service.ConsultaExpedienteService;

@Controller
public class ExpedienteDetalleController
{
	private final ConsultaExpedienteService consultaService;

	public ExpedienteDetalleController(ConsultaExpedienteService consultaService)
	{
		this.consultaService = consultaService;
	}

	@GetMapping("/solicitante/expedientes/{id}")
	public String detalle(@PathVariable Integer id, HttpSession session,
			Model model, RedirectAttributes redirect)
	{
		UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

		Optional<DetalleExpedienteDTO> detalle = consultaService.buscar(id, usuario.getId());

		if (detalle.isEmpty())
		{
			redirect.addFlashAttribute("error", "El expediente no existe.");
			return "redirect:/solicitante/expedientes";
		}

		model.addAttribute("detalle", detalle.get());
		return "solicitante/expediente-detalle";
	}
}