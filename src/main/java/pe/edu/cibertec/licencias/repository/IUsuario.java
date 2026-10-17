package pe.edu.cibertec.licencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.Usuario;

@Repository
public interface IUsuario extends JpaRepository<Usuario, Integer>
{
	boolean existsByDni(String dni);

	boolean existsByCorreo(String correo);
	
	boolean existsByDniAndCodigoNot(String dni, Integer codigo);

	boolean existsByCorreoAndCodigoNot(String correo, Integer codigo);
}
