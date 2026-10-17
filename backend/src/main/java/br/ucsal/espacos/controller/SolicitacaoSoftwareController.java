package br.ucsal.espacos.controller;

import java.util.List;
import java.util.Map;

import br.ucsal.espacos.model.SolicitacaoSoftware;
import br.ucsal.espacos.service.SolicitacaoSoftwareService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SolicitacaoSoftwareController {

    @Autowired
    private SolicitacaoSoftwareService service;

    @GetMapping("/api/usuario/solicitacoes-software")
    public List<SolicitacaoSoftware> minhas() {
        return service.minhas();
    }

    @PostMapping("/api/usuario/solicitacoes-software")
    public SolicitacaoSoftware salvar(@Valid @RequestBody SolicitacaoSoftware solicitacao) {
        return service.salvar(solicitacao);
    }

    @PutMapping("/api/usuario/solicitacoes-software/{id}")
    public SolicitacaoSoftware atualizar(@PathVariable Long id, @Valid @RequestBody SolicitacaoSoftware solicitacao) {
        return service.atualizar(id, solicitacao);
    }

    @PatchMapping("/api/usuario/solicitacoes-software/{id}/inativar")
    public SolicitacaoSoftware inativar(@PathVariable Long id) {
        return service.inativar(id);
    }

    @GetMapping("/api/colaborador/solicitacoes-software")
    public List<SolicitacaoSoftware> doMeuCentro() {
        return service.doMeuCentro();
    }

    @PatchMapping("/api/colaborador/solicitacoes-software/{id}/aprovar")
    public SolicitacaoSoftware aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @PatchMapping("/api/colaborador/solicitacoes-software/{id}/reprovar")
    public SolicitacaoSoftware reprovar(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        return service.reprovar(id, dados.get("justificativa"));
    }
}
