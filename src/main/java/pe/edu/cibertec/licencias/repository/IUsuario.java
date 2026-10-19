package pe.edu.cibertec.licencias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Usuario;

public interface IUsuario extends JpaRepository<Usuario, Integer>
{
   
    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByRolAndActivoTrue(String rol);

    boolean existsByDni(String dni);

    boolean existsByCorreo(String correo);

    boolean existsByDniAndIdUsuarioNot(String dni, Integer idUsuario);

    boolean existsByCorreoAndIdUsuarioNot(String correo, Integer idUsuario);
}