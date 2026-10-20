package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.entity.Licencia;
import pe.edu.cibertec.licencias.service.FirmaService;
import pe.edu.cibertec.licencias.service.ReglaNegocioException;



@Controller
@RequestMapping("/subgerente/bandeja")
@RequiredArgsConstructor
public class FirmaController
{
    private final FirmaService firma;

    @GetMapping
    public String bandeja(Model model)
    {
        model.addAttribute("expedientes", firma.pendientesDeFirma());
        return "subgerente/bandeja";
    }

    @PostMapping("/{id}/firmar")
    public String firmar(@PathVariable Integer id, HttpSession session, RedirectAttributes flash)
    {
        try
        {
            UsuarioSesion u = (UsuarioSesion) session.getAttribute("usuario");
            Licencia l = firma.firmar(id, u.getId());
            flash.addFlashAttribute("ok", "Licencia " + l.getNumero() + " emitida.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/subgerente/bandeja";
    }
}
