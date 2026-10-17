package br.ucsal.espacos.controller;

import java.util.List;

import br.ucsal.espacos.model.Ies;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.service.IesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ies")
public class IesController {

    @Autowired
    private IesService service;

    @GetMapping
    public List<Ies> listar() {
        return service.listar();
    }

    @PostMapping
    public Ies salvar(@Valid @RequestBody Ies ies) {
        return service.salvar(ies);
    }

    @PutMapping("/{id}")
    public Ies atualizar(@PathVariable Long id, @Valid @RequestBody Ies ies) {
        return service.atualizar(id, ies);
    }

    @PatchMapping("/{id}/inativar")
    public Ies inativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.INATIVO);
    }

    @PatchMapping("/{id}/reativar")
    public Ies reativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.ATIVO);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
