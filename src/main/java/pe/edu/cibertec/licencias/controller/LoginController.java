package pe.edu.cibertec.licencias.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.entity.Usuario;
import pe.edu.cibertec.licencias.repository.IUsuario;


@Controller
public class LoginController
{
    private final IUsuario usuarioRepo;
    private final PasswordEncoder passwordEncoder;
 
    public LoginController(IUsuario usuarioRepo, PasswordEncoder passwordEncoder)
    {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session)
    {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario != null)
        {
            return "redirect:" + usuario.getRutaInicio();
        }
        return "login";   
    }

 
    @PostMapping("/login")
    public String ingresar(@RequestParam String correo,
                           @RequestParam String clave,
                           HttpSession session,
                           RedirectAttributes flash)
    {
        Optional<Usuario> encontrado = usuarioRepo.findByCorreo(correo.trim());

        boolean valido = encontrado.isPresent()
                && Boolean.TRUE.equals(encontrado.get().getActivo())
                && passwordEncoder.matches(clave, encontrado.get().getClave());

        if (!valido)
        {
            flash.addFlashAttribute("error", "Correo o contraseña incorrectos.");
            flash.addFlashAttribute("correo", correo);   // para no obligarla a reescribir el correo
            return "redirect:/login";
        }

        Usuario u = encontrado.get();
        UsuarioSesion usuario = new UsuarioSesion(
                u.getIdUsuario(), u.getNombres(), u.getApellidos(), u.getCorreo(), u.getRol());
        session.setAttribute("usuario", usuario);

        return "redirect:" + usuario.getRutaInicio();
    }

    
    @GetMapping("/logout")
    public String salir(HttpSession session)
    {
        session.invalidate();
        return "redirect:/login?salio";
    }
}