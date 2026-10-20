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
import pe.edu.cibertec.licencias.service.EvaluacionService;
import pe.edu.cibertec.licencias.service.ReglaNegocioException;


// lezama
@Controller
@RequestMapping("/tecnico")
@RequiredArgsConstructor
public class TecnicoController
{
    private final EvaluacionService evaluacion;
    private final DetalleExpedienteService detalles;
    private final DocumentoService documentoService;

    @GetMapping("/bandeja")
    public String bandeja(HttpSession session, Model model)
    {
        model.addAttribute("filas", evaluacion.bandeja(idUsuario(session)));
        return "tecnico/bandeja";
    }

    @GetMapping("/evaluar/{id}")
    public String evaluar(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes flash)
    {
        try
        {
            evaluacion.cargarAsignado(id, idUsuario(session));
            model.addAttribute("d", detalles.cargar(id));
            model.addAttribute("rutaDocs", "/tecnico/evaluar/" + id + "/documentos/");
            return "tecnico/evaluar";
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/tecnico/bandeja";
        }
    }

    @PostMapping("/evaluar/{id}/aprobar")
    public String aprobar(@PathVariable Integer id, HttpSession session, RedirectAttributes flash)
    {
        try
        {
            Expediente e = evaluacion.aprobar(id, idUsuario(session));
            flash.addFlashAttribute("ok", "Expediente " + e.getNumero() + " aprobado. Pasa a firma del Sub Gerente.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/tecnico/evaluar/" + id;
        }
        return "redirect:/tecnico/bandeja";
    }

    @PostMapping("/evaluar/{id}/denegar")
    public String denegar(@PathVariable Integer id, @RequestParam String motivo,
                          HttpSession session, RedirectAttributes flash)
    {
        try
        {
            Expediente e = evaluacion.denegar(id, idUsuario(session), motivo);
            flash.addFlashAttribute("ok", "Expediente " + e.getNumero() + " denegado. Se registró el motivo.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/tecnico/evaluar/" + id;
        }
        return "redirect:/tecnico/bandeja";
    }

    @GetMapping("/evaluar/{id}/documentos/{idDoc}")
    public ResponseEntity<Resource> documento(@PathVariable Integer id, @PathVariable Integer idDoc,
                                              HttpSession session)
    {
        try
        {
            evaluacion.cargarAsignado(id, idUsuario(session));       // SOLO LOS DE SUS EXPEDIENTES
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
