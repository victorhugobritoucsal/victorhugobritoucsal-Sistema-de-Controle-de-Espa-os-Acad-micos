package br.ucsal.espacos.controller;

import java.util.List;

import br.ucsal.espacos.model.CentroAcademico;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.service.CentroAcademicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/centros")
public class CentroAcademicoController {

    @Autowired
    private CentroAcademicoService service;

    @GetMapping
    public List<CentroAcademico> listar() {
        return service.listar();
    }

    @PostMapping
    public CentroAcademico salvar(@Valid @RequestBody CentroAcademico centroAcademico) {
        return service.salvar(centroAcademico);
    }

    @PutMapping("/{id}")
    public CentroAcademico atualizar(@PathVariable Long id, @Valid @RequestBody CentroAcademico centroAcademico) {
        return service.atualizar(id, centroAcademico);
    }

    @PatchMapping("/{id}/inativar")
    public CentroAcademico inativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.INATIVO);
    }

    @PatchMapping("/{id}/reativar")
    public CentroAcademico reativar(@PathVariable Long id) {
        return service.alterarStatus(id, Status.ATIVO);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
