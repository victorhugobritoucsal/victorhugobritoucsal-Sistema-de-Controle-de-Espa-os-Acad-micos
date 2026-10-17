package br.ucsal.espacos.controller;

import java.util.List;

import br.ucsal.espacos.model.Curso;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cursos")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public List<Curso> listar() {
        return service.listar();
    }

    @PostMapping
    public Curso salvar(@Valid @RequestBody Curso curso) {
        return service.salvar(curso);
    }

    @PutMapping("/{id}")
    public Curso atualizar(@PathVariable Long id, @Valid @RequestBody Curso curso) {
        return service.atualizar(id, curso);
    }

    @PatchMapping("/{id}/inativar")
    public Curso inativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.INATIVO);
    }

    @PatchMapping("/{id}/reativar")
    public Curso reativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.ATIVO);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
