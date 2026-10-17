package pe.edu.cibertec.licencias.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.dto.CuentaDTO;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IUsuario;

@Service
public class UsuarioService
{
	private final IUsuario usuarioRepository;
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public UsuarioService(IUsuario usuarioRepository)
	{
		this.usuarioRepository = usuarioRepository;
	}

	public boolean existeDni(String dni)
	{
		return usuarioRepository.existsByDni(dni);
	}

	public boolean existeCorreo(String correo)
	{
		return usuarioRepository.existsByCorreo(correo);
	}

	public Usuario crearCuenta(CuentaDTO dto)
	{
		Usuario usuario = new Usuario();
		usuario.setDni(dto.getDni());
		usuario.setNombres(dto.getNombres().trim());
		usuario.setApellidos(dto.getApellidos().trim());
		usuario.setCorreo(dto.getCorreo().trim());

		String telefono = dto.getTelefono();
		usuario.setTelefono(telefono == null || telefono.isBlank() ? null : telefono);

		usuario.setContrasena(encoder.encode(dto.getClave()));
		usuario.setRol("ADMINISTRADO");
		usuario.setDisponible(true);
		usuario.setActivo(true);

		return usuarioRepository.save(usuario);
	}
}