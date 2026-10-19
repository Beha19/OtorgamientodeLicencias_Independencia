package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.service.ExpedienteService;

@Controller
@RequestMapping("/solicitante/expedientes")
public class ExpedienteController
{
	private final ExpedienteService expedienteService;

	public ExpedienteController(ExpedienteService expedienteService)
	{
		this.expedienteService = expedienteService;
	}

	@GetMapping
	public String mis(HttpSession session, Model model)
	{
		UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

		model.addAttribute("expedientes", expedienteService.listarPorSolicitante(usuario.getId()));
		return "expedientes/mis";
	}
}