package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CuentaController
{
	@GetMapping("/cuenta/crear")
	public String crear()
	{
		return "cuenta/crear";
	}
}
