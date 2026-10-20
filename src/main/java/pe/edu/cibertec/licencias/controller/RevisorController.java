package pe.edu.cibertec.licencias.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.service.DetalleExpedienteService;
import pe.edu.cibertec.licencias.service.DocumentoService;
import pe.edu.cibertec.licencias.service.ReglaNegocioException;
import pe.edu.cibertec.licencias.service.RevisionService;


//lezama
@Controller
@RequestMapping("/revisor")
@RequiredArgsConstructor
public class RevisorController
{
    private final RevisionService revision;
    private final DetalleExpedienteService detalles;
    private final DocumentoService documentoService;

    @GetMapping("/bandeja")
    public String bandeja(Model model)
    {
        model.addAttribute("filas", revision.bandeja());
        return "revisor/bandeja";
    }

    @GetMapping("/expedientes/{id}")
    public String detalle(@PathVariable Integer id, Model model, RedirectAttributes flash)
    {
        try
        {
            revision.exigirPendiente(id);
            model.addAttribute("d", detalles.cargar(id));
            model.addAttribute("rutaDocs", "/revisor/expedientes/" + id + "/documentos/");
            return "revisor/detalle";
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/revisor/bandeja";
        }
    }

    @PostMapping("/expedientes/{id}/verificar")
    public String verificar(@PathVariable Integer id, HttpSession session, RedirectAttributes flash)
    {
        try
        {
            Expediente e = revision.verificar(id, idUsuario(session));
            flash.addFlashAttribute("ok", "Expediente " + e.getNumero() + " verificado y derivado a "
                    + e.getTecnicoAsignado().getNombres() + " " + e.getTecnicoAsignado().getApellidos());
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/revisor/expedientes/" + id;
        }
        return "redirect:/revisor/bandeja";
    }

    @PostMapping("/expedientes/{id}/observar")
    public String observar(@PathVariable Integer id, @RequestParam String descripcion,
                           HttpSession session, RedirectAttributes flash)
    {
        try
        {
            Expediente e = revision.observar(id, idUsuario(session), descripcion);
            flash.addFlashAttribute("ok", "Expediente " + e.getNumero() + " observado. Se registró el oficio.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/revisor/expedientes/" + id;
        }
        return "redirect:/revisor/bandeja";
    }

    @GetMapping("/expedientes/{id}/documentos/{idDoc}")
    public ResponseEntity<Resource> documento(@PathVariable Integer id, @PathVariable Integer idDoc)
    {
        try
        {
            DocumentoAdjunto d = detalles.buscarDocumento(idDoc, id);
            return RespuestaArchivo.enLinea(d, documentoService.recurso(d));
        }
        catch (ReglaNegocioException ex)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private Integer idUsuario(HttpSession session)
    {
        return ((UsuarioSesion) session.getAttribute("usuario")).getId();
    }
}
