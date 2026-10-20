
package pe.edu.cibertec.licencias.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.cibertec.licencias.entity.Zona;
import pe.edu.cibertec.licencias.repository.IZona;

@Controller
@RequestMapping("/admin/zonas")
public class ZonaController {

    private final IZona zonaRepository;

    public ZonaController(IZona zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas",
                zonaRepository.findAll());
        return "admin/zonas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("zona", new Zona());
        return "admin/zona-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Zona no encontrada"));

        model.addAttribute("zona", zona);
        return "admin/zona-form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Zona zona,
            RedirectAttributes redirect) {

        if (zona.getIdZona() == null) {
            zona.setActivo(true);
        }

        zonaRepository.save(zona);

        redirect.addFlashAttribute(
                "mensaje", "Zona guardada correctamente.");

        return "redirect:/admin/zonas";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Integer id,
            RedirectAttributes redirect) {

        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Zona no encontrada"));

        zona.setActivo(false);
        zonaRepository.save(zona);

        redirect.addFlashAttribute(
                "mensaje", "Zona desactivada correctamente.");

        return "redirect:/admin/zonas";
    }
}
