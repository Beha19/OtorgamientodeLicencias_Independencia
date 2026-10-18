package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InicioController
{
    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/solicitante/inicio")
    public String solicitante()
    {
        return "solicitante/inicio";
    }

    @GetMapping("/revisor/inicio")
    public String revisor()
    {
        return "revisor/inicio";
    }

    @GetMapping("/tecnico/inicio")
    public String tecnico()
    {
        return "tecnico/inicio";
    }

    @GetMapping("/subgerente/inicio")
    public String subgerente()
    {
        return "subgerente/inicio";
    }

    @GetMapping("/admin/inicio")
    public String admin()
    {
        return "admin/inicio";
    }
}