package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.licencias.service.ExpedienteService;

@Controller
@RequestMapping("/expedientes")
public class ExpedienteController
{
	private final ExpedienteService expedienteService;

	public ExpedienteController(ExpedienteService expedienteService)
	{
		this.expedienteService = expedienteService;
	}

	@GetMapping("/mis")
	public String mis(@RequestParam(required = false) Integer usuario,
			HttpSession session, Model model)
	{
		Integer codigoUsuario = usuario != null ? usuario : (Integer) session.getAttribute("usuarioId");

		if (codigoUsuario == null)
		{
			model.addAttribute("sinUsuario", true);
			return "expedientes/mis";
		}

		model.addAttribute("expedientes", expedienteService.listarPorAdministrado(codigoUsuario));
		return "expedientes/mis";
	}
}