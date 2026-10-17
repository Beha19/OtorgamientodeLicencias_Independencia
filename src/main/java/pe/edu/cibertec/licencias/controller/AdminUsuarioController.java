package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController
{
	@GetMapping
	public String lista()
	{
		return "admin/usuarios";
	}

	@GetMapping("/nuevo")
	public String nuevo()
	{
		return "admin/usuario-form";
	}
}