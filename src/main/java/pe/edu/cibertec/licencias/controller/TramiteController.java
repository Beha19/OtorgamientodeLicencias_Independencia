package pe.edu.cibertec.licencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.SolicitudForm;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.repository.ICondicionSeguridad;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.IGiro;
import pe.edu.cibertec.licencias.repository.IPago;
import pe.edu.cibertec.licencias.service.ExpedienteService;
import pe.edu.cibertec.licencias.service.ReglaNegocioException;


@Controller
@RequestMapping("/solicitante/tramite")
@RequiredArgsConstructor
public class TramiteController
{
    private final ExpedienteService servicio;
    private final IExpediente expedientes;
    private final IGiro giros;
    private final ICondicionSeguridad condiciones;
    private final IPago pagos;

    // FORMULARIO VACIO
    @GetMapping("/nuevo")
    public String nuevo(Model model)
    {
        model.addAttribute("form", new SolicitudForm());
        cargarCatalogos(model);
        return "solicitante/nuevo-tramite";
    }

    // GUARDAR BORRADOR
    @PostMapping
    public String guardar(@Valid @ModelAttribute("form") SolicitudForm form,
                          BindingResult errores, Model model, HttpSession session)
    {
        if (errores.hasErrors())
        {
            cargarCatalogos(model);
            return "solicitante/nuevo-tramite";
        }
        try
        {
            Expediente e = servicio.guardarBorrador(form, idSolicitante(session));
            return "redirect:/solicitante/tramite/" + e.getIdExpediente();
        }
        catch (ReglaNegocioException ex)
        {
            model.addAttribute("errorNegocio", ex.getMessage());
            cargarCatalogos(model);
            return "solicitante/nuevo-tramite";
        }
    }

    // DETALLE DEL TRÁMITE (BOTONES DE PAGAR / ENVIAR) 
    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model, HttpSession session)
    {
        Expediente e = expedientes.findById(id).orElse(null);
        if (e == null || !e.getSolicitante().getIdUsuario().equals(idSolicitante(session)))
        {
            return "redirect:/solicitante/inicio";
        }
        model.addAttribute("exp", e);
        model.addAttribute("pagos", pagos.findByExpediente_IdExpediente(id));
        return "solicitante/tramite";
    }

    @PostMapping("/{id}/pagar")
    public String pagar(@PathVariable Integer id, @RequestParam String medio,
                        HttpSession session, RedirectAttributes flash)
    {
        try
        {
            servicio.pagar(id, idSolicitante(session), medio);
            flash.addFlashAttribute("ok", "Pago registrado. Ya puedes enviar tu trámite.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/solicitante/tramite/" + id;
    }

    @PostMapping("/{id}/enviar")
    public String enviar(@PathVariable Integer id, HttpSession session, RedirectAttributes flash)
    {
        try
        {
            Expediente e = servicio.enviar(id, idSolicitante(session));
            flash.addFlashAttribute("ok", "Trámite enviado. Tu número de expediente es " + e.getNumero());
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/solicitante/tramite/" + id;
    }

    private void cargarCatalogos(Model model)
    {
        model.addAttribute("giros", giros.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("condiciones", condiciones.findByActivoTrueOrderByGrupoAscIdCondicionAsc());
    }

    private Integer idSolicitante(HttpSession session)
    {
        return ((UsuarioSesion) session.getAttribute("usuario")).getId();
    }
}