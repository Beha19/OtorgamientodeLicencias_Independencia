package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/feriados")
public class AdminFeriadoController
{
	@GetMapping
	public String lista()
	{
		return "admin/feriados";
	}
}