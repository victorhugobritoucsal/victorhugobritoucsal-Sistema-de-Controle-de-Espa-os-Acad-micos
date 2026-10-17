package br.ucsal.espacos.controller;

import java.util.List;

import br.ucsal.espacos.model.LogAuditoria;
import br.ucsal.espacos.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditoriaController {

    @Autowired
    private AuditoriaService service;

    @GetMapping("/api/admin/auditoria")
    public List<LogAuditoria> listar() {
        return service.listar();
    }
}
