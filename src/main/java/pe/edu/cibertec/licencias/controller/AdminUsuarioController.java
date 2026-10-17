package pe.edu.cibertec.licencias.controller;

import java.util.List;
import java.util.Optional;

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
import pe.edu.cibertec.licencias.dto.UsuarioDTO;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.service.UsuarioService;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController
{
	private static final List<String> ROLES =
			List.of("ADMINISTRADO", "REVISOR", "TECNICO", "SUBGERENTE", "ADMIN");

	private final UsuarioService usuarioService;

	public AdminUsuarioController(UsuarioService usuarioService)
	{
		this.usuarioService = usuarioService;
	}

	
	@ModelAttribute("roles")
	public List<String> roles()
	{
		return ROLES;
	}

	@GetMapping
	public String lista(Model model)
	{
		model.addAttribute("usuarios", usuarioService.listar());
		return "admin/usuarios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model)
	{
		UsuarioDTO dto = new UsuarioDTO();
		dto.setRol("ADMINISTRADO");
		model.addAttribute("usuario", dto);
		return "admin/usuario-form";
	}

	@GetMapping("/{codigo}/editar")
	public String editar(@PathVariable Integer codigo, Model model, RedirectAttributes redirect)
	{
		Optional<Usuario> encontrado = usuarioService.buscarPorCodigo(codigo);

		if (encontrado.isEmpty())
		{
			redirect.addFlashAttribute("error", "El usuario no existe.");
			return "redirect:/admin/usuarios";
		}

		Usuario u = encontrado.get();
		UsuarioDTO dto = new UsuarioDTO();
		dto.setCodigo(u.getCodigo());
		dto.setDni(u.getDni());
		dto.setNombres(u.getNombres());
		dto.setApellidos(u.getApellidos());
		dto.setCorreo(u.getCorreo());
		dto.setTelefono(u.getTelefono());
		dto.setRol(u.getRol());
	

		model.addAttribute("usuario", dto);
		return "admin/usuario-form";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("usuario") UsuarioDTO usuario,
			BindingResult result, RedirectAttributes redirect)
	{
		if (!result.hasFieldErrors("dni") && usuarioService.dniRepetido(usuario.getDni(), usuario.getCodigo()))
		{
			result.rejectValue("dni", "duplicado", "Ya existe otro usuario con ese DNI");
		}

		if (!result.hasFieldErrors("correo") && usuarioService.correoRepetido(usuario.getCorreo(), usuario.getCodigo()))
		{
			result.rejectValue("correo", "duplicado", "Ya existe otro usuario con ese correo");
		}


		if (usuario.getCodigo() == null && !result.hasFieldErrors("clave")
				&& (usuario.getClave() == null || usuario.getClave().isBlank()))
		{
			result.rejectValue("clave", "obligatoria", "Ingrese la contraseña");
		}

		if (result.hasErrors())
		{
			return "admin/usuario-form";
		}

		boolean esNuevo = usuario.getCodigo() == null;
		usuarioService.guardar(usuario);
		redirect.addFlashAttribute("mensaje",
				esNuevo ? "Usuario creado correctamente." : "Usuario actualizado correctamente.");
		return "redirect:/admin/usuarios";
	}

	@PostMapping("/{codigo}/desactivar")
	public String desactivar(@PathVariable Integer codigo, RedirectAttributes redirect)
	{
		usuarioService.desactivar(codigo);
		redirect.addFlashAttribute("mensaje", "Usuario desactivado.");
		return "redirect:/admin/usuarios";
	}
}