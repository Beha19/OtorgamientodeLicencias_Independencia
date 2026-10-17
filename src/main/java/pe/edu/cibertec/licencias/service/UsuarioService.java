package pe.edu.cibertec.licencias.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.licencias.dto.CuentaDTO;
import pe.edu.cibertec.licencias.dto.UsuarioDTO;
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

	// ---------- Crear cuenta (Tarea 2) ----------

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



	public List<Usuario> listar()
	{
		return usuarioRepository.findAll(Sort.by("codigo"));
	}

	public Optional<Usuario> buscarPorCodigo(Integer codigo)
	{
		return usuarioRepository.findById(codigo);
	}

	
	public boolean dniRepetido(String dni, Integer codigo)
	{
		return codigo == null
				? usuarioRepository.existsByDni(dni)
				: usuarioRepository.existsByDniAndCodigoNot(dni, codigo);
	}

	public boolean correoRepetido(String correo, Integer codigo)
	{
		return codigo == null
				? usuarioRepository.existsByCorreo(correo)
				: usuarioRepository.existsByCorreoAndCodigoNot(correo, codigo);
	}

	public Usuario guardar(UsuarioDTO dto)
	{
		Usuario usuario;

		if (dto.getCodigo() == null)
		{
			usuario = new Usuario();
			usuario.setDisponible(true);
			usuario.setActivo(true);
		}
		else
		{
			usuario = usuarioRepository.findById(dto.getCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		}

		usuario.setDni(dto.getDni());
		usuario.setNombres(dto.getNombres().trim());
		usuario.setApellidos(dto.getApellidos().trim());
		usuario.setCorreo(dto.getCorreo().trim());

		String telefono = dto.getTelefono();
		usuario.setTelefono(telefono == null || telefono.isBlank() ? null : telefono);

		usuario.setRol(dto.getRol());

	
		if (dto.getClave() != null && !dto.getClave().isBlank())
		{
			usuario.setContrasena(encoder.encode(dto.getClave()));
		}

		return usuarioRepository.save(usuario);
	}

	
	public void desactivar(Integer codigo)
	{
		usuarioRepository.findById(codigo).ifPresent(usuario ->
		{
			usuario.setActivo(false);
			usuarioRepository.save(usuario);
		});
	}
}