package br.ucsal.espacos.controller;

import java.util.List;
import java.util.Map;

import br.ucsal.espacos.model.SolicitacaoEspaco;
import br.ucsal.espacos.service.SolicitacaoEspacoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SolicitacaoEspacoController {

    @Autowired
    private SolicitacaoEspacoService service;

    @GetMapping("/api/usuario/solicitacoes-espaco")
    public List<SolicitacaoEspaco> minhas() {
        return service.minhas();
    }

    @PostMapping("/api/usuario/solicitacoes-espaco")
    public SolicitacaoEspaco salvar(@Valid @RequestBody SolicitacaoEspaco solicitacao) {
        return service.salvar(solicitacao);
    }

    @PutMapping("/api/usuario/solicitacoes-espaco/{id}")
    public SolicitacaoEspaco atualizar(@PathVariable Long id, @Valid @RequestBody SolicitacaoEspaco solicitacao) {
        return service.atualizar(id, solicitacao);
    }

    @PatchMapping("/api/usuario/solicitacoes-espaco/{id}/inativar")
    public SolicitacaoEspaco inativar(@PathVariable Long id) {
        return service.inativar(id);
    }

    @PatchMapping("/api/usuario/solicitacoes-espaco/{id}/liberar")
    public SolicitacaoEspaco liberar(@PathVariable Long id) {
        return service.liberar(id);
    }

    @GetMapping("/api/colaborador/solicitacoes-espaco")
    public List<SolicitacaoEspaco> doMeuCentro() {
        return service.doMeuCentro();
    }

    @PatchMapping("/api/colaborador/solicitacoes-espaco/{id}/aprovar")
    public SolicitacaoEspaco aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @PatchMapping("/api/colaborador/solicitacoes-espaco/{id}/reprovar")
    public SolicitacaoEspaco reprovar(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        return service.reprovar(id, dados.get("justificativa"));
    }
}
