package pe.edu.cibertec.licencias.config;

import java.io.IOException;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;

public class RolInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException
    {
        String contexto = request.getContextPath();
        HttpSession session = request.getSession(false);
        UsuarioSesion usuario = session == null ? null : (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null)
        {
            response.sendRedirect(contexto + "/login");
            return false;
        }

        String ruta = request.getRequestURI().substring(contexto.length());
        String rolDeLaRuta = ruta.split("/")[1].toUpperCase();

        if (!rolDeLaRuta.equals(usuario.getRol()))
        {
            response.sendRedirect(contexto + usuario.getRutaInicio());
            return false;
        }
        return true;
    }
}