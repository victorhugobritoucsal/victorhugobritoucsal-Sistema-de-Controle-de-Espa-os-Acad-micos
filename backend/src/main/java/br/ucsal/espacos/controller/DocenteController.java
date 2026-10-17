package br.ucsal.espacos.controller;

import java.util.List;
import java.util.Map;

import br.ucsal.espacos.model.Docente;
import br.ucsal.espacos.model.HistoricoDocente;
import br.ucsal.espacos.service.DocenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/docentes")
public class DocenteController {

    @Autowired
    private DocenteService service;

    @GetMapping
    public List<Docente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoDocente> historico(@PathVariable Long id) {
        return service.historico(id);
    }

    @PostMapping
    public Docente salvar(@Valid @RequestBody Docente docente) {
        return service.salvar(docente);
    }

    @PutMapping("/{id}")
    public Docente atualizar(@PathVariable Long id, @Valid @RequestBody Docente docente) {
        return service.atualizar(id, docente);
    }

    @PatchMapping("/{id}/inativar")
    public Docente inativar(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        return service.inativar(id, dados.get("motivo"));
    }

    @PatchMapping("/{id}/reativar")
    public Docente reativar(@PathVariable Long id) {
        return service.reativar(id);
    }
}
