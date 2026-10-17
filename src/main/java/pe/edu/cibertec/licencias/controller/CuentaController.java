package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.edu.cibertec.licencias.dto.CuentaDTO;
import pe.edu.cibertec.licencias.service.UsuarioService;

@Controller
@RequestMapping("/cuenta")
public class CuentaController
{
	private final UsuarioService usuarioService;

	public CuentaController(UsuarioService usuarioService)
	{
		this.usuarioService = usuarioService;
	}

	@GetMapping("/crear")
	public String crear(Model model)
	{
		model.addAttribute("cuenta", new CuentaDTO());
		return "cuenta/crear";
	}

	@PostMapping("/crear")
	public String guardar(@Valid @ModelAttribute("cuenta") CuentaDTO cuenta,
			BindingResult result, RedirectAttributes redirect)
	{
		if (!result.hasFieldErrors("dni") && usuarioService.existeDni(cuenta.getDni()))
		{
			result.rejectValue("dni", "duplicado", "Ya existe una cuenta con ese DNI");
		}

		if (!result.hasFieldErrors("correo") && usuarioService.existeCorreo(cuenta.getCorreo()))
		{
			result.rejectValue("correo", "duplicado", "Ya existe una cuenta con ese correo");
		}

		if (result.hasErrors())
		{
			return "cuenta/crear";
		}

		usuarioService.crearCuenta(cuenta);
		redirect.addFlashAttribute("mensaje", "Cuenta creada correctamente.");
		return "redirect:/cuenta/crear";
	}
}