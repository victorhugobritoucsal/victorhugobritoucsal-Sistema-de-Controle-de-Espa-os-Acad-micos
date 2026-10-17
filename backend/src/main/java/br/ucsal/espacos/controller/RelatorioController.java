package br.ucsal.espacos.controller;

import java.util.Map;

import br.ucsal.espacos.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    @GetMapping("/api/admin/relatorios/institucional")
    public Map<String, Object> institucional() {
        return service.institucional();
    }

    @GetMapping("/api/admin/relatorios/docentes")
    public Map<String, Object> docentes() {
        return service.docentes();
    }

    @GetMapping("/api/colaborador/relatorio")
    public Map<String, Object> colaborador() {
        return service.colaborador();
    }

    @GetMapping("/api/usuario/relatorio")
    public Map<String, Object> usuario() {
        return service.usuario();
    }
}
