
package pe.edu.cibertec.licencias.controller;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.cibertec.licencias.entity.Giro;
import pe.edu.cibertec.licencias.entity.Zona;
import pe.edu.cibertec.licencias.repository.IGiro;
import pe.edu.cibertec.licencias.repository.IZona;

@Controller
@RequestMapping("/admin/giros")
public class GiroController {

    private final IGiro giroRepository;
    private final IZona zonaRepository;

    public GiroController(IGiro giroRepository, IZona zonaRepository) {
        this.giroRepository = giroRepository;
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listar(Model model) {
        List<Giro> giros = giroRepository.findAllWithZonas();
        model.addAttribute("giros", giros);
        return "admin/giros";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("giro", new Giro());
        prepararFormulario(model);
        return "admin/giro-form";
    }

    @GetMapping("/editar/{id}")
    @Transactional(readOnly = true)
    public String editar(@PathVariable Integer id, Model model) {
        Giro giro = giroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Giro no encontrado"));

        model.addAttribute("giro", giro);
        model.addAttribute("zonasSeleccionadas",
                giro.getZonasPermitidas().stream()
                    .map(Zona::getIdZona)
                    .toList());

        prepararFormulario(model);
        return "admin/giro-form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Giro giro,
            @RequestParam(name = "zonasIds", required = false) List<Integer> zonasIds,
            RedirectAttributes redirect) {

        Giro giroGuardar;

        if (giro.getIdGiro() != null) {
            giroGuardar = giroRepository.findById(giro.getIdGiro())
                    .orElseThrow(() -> new IllegalArgumentException("Giro no encontrado"));
        } else {
            giroGuardar = new Giro();
            giroGuardar.setActivo(true);
        }

        giroGuardar.setCodigoCiiu(giro.getCodigoCiiu());
        giroGuardar.setNombre(giro.getNombre());
        giroGuardar.setNivelRiesgo(giro.getNivelRiesgo());

        if (zonasIds == null || zonasIds.isEmpty()) {
            giroGuardar.setZonasPermitidas(new HashSet<>());
        } else {
            giroGuardar.setZonasPermitidas(
                new HashSet<>(zonaRepository.findAllById(zonasIds)));
        }

        giroRepository.save(giroGuardar);
        redirect.addFlashAttribute("mensaje", "Giro guardado correctamente.");

        return "redirect:/admin/giros";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id,
                             RedirectAttributes redirect) {
        Giro giro = giroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Giro no encontrado"));

        giro.setActivo(false);
        giroRepository.save(giro);

        redirect.addFlashAttribute("mensaje", "Giro desactivado correctamente.");
        return "redirect:/admin/giros";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("zonas",
                zonaRepository.findByActivoTrueOrderByNombreAsc());
    }
}
