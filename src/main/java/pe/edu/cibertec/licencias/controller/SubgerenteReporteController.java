package pe.edu.cibertec.licencias.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.cibertec.licencias.entity.Licencia;
import pe.edu.cibertec.licencias.repository.ILicencia;

@Controller
public class SubgerenteReporteController
{
    private final ILicencia licenciaRepository;

    public SubgerenteReporteController(ILicencia licenciaRepository)
    {
        this.licenciaRepository = licenciaRepository;
    }

    @GetMapping("/subgerente/reportes")
    public String reportes(Model model)
    {
        List<Licencia> licencias = licenciaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaEmision"));
        model.addAttribute("licencias", licencias);
        return "subgerente/reportes";
    }
}