package pe.edu.cibertec.licencias.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.dto.SolicitudForm;
import pe.edu.cibertec.licencias.dto.UsuarioSesion;
import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;
import pe.edu.cibertec.licencias.entity.Expediente;
import pe.edu.cibertec.licencias.entity.Usuario;                        // NUEVO
import pe.edu.cibertec.licencias.repository.ICondicionSeguridad;
import pe.edu.cibertec.licencias.repository.IDocumentoAdjunto;
import pe.edu.cibertec.licencias.repository.IExpediente;
import pe.edu.cibertec.licencias.repository.IGiro;
import pe.edu.cibertec.licencias.repository.IPago;
import pe.edu.cibertec.licencias.repository.IUsuario;                   // NUEVO
import pe.edu.cibertec.licencias.service.DocumentoService;
import pe.edu.cibertec.licencias.service.ExpedienteService;
import pe.edu.cibertec.licencias.service.ReglaNegocioException;


@Controller
@RequestMapping("/solicitante/tramite")
@RequiredArgsConstructor
public class TramiteController
{
    private final ExpedienteService servicio;
    private final DocumentoService documentoService;
    private final IExpediente expedientes;
    private final IGiro giros;
    private final ICondicionSeguridad condiciones;
    private final IPago pagos;
    private final IDocumentoAdjunto documentos;
    private final IUsuario usuarios;                                    // NUEVO

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

    // DETALLE DEL TRÁMITE (BOTONES DE PAGAR / ENVIAR / DOCUMENTOS)
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
        model.addAttribute("documentos", documentos.findByExpediente_IdExpediente(id));
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

    // SUBIR UN DOCUMENTO (DNI, CARTA PODER, VIGENCIA DE PODER)
    @PostMapping("/{id}/documentos")
    public String subirDocumento(@PathVariable Integer id,
                                 @RequestParam String tipo,
                                 @RequestParam("archivo") MultipartFile archivo,
                                 HttpSession session, RedirectAttributes flash)
    {
        try
        {
            documentoService.subir(id, idSolicitante(session), tipo, archivo);
            flash.addFlashAttribute("ok", "Documento adjuntado correctamente.");
        }
        catch (ReglaNegocioException ex)
        {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/solicitante/tramite/" + id;
    }

    // VER UN DOCUMENTO (SOLO SU DUEÑO). SI NO ES SUYO O NO EXISTE: 404
    @GetMapping("/{id}/documentos/{idDoc}")
    public ResponseEntity<Resource> verDocumento(@PathVariable Integer id,
                                                 @PathVariable Integer idDoc,
                                                 HttpSession session)
    {
        try
        {
            DocumentoAdjunto d = documentoService.buscar(idDoc, id, idSolicitante(session));
            Resource archivo = documentoService.recurso(d);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline().filename(d.getNombreArchivo()).build().toString())
                    .contentType(MediaTypeFactory.getMediaType(archivo)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(archivo);
        }
        catch (ReglaNegocioException ex)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // NUEVO: PONE EL USUARIO COMPLETO (CON DNI Y TELEFONO) EN EL MODELO DE LAS VISTAS DE ESTE CONTROLADOR
    @ModelAttribute("perfil")
    public Usuario perfil(HttpSession session)
    {
        return usuarios.findById(idSolicitante(session)).orElseThrow();
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