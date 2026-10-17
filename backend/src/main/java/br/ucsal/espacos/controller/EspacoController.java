package br.ucsal.espacos.controller;

import java.util.List;
import java.util.Map;

import br.ucsal.espacos.model.Espaco;
import br.ucsal.espacos.service.EspacoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EspacoController {

    @Autowired
    private EspacoService service;

    @GetMapping("/api/colaborador/espacos")
    public List<Espaco> listarDoMeuCentro() {
        return service.listarDoMeuCentro();
    }

    @GetMapping("/api/usuario/espacos")
    public List<Espaco> listarAtivos() {
        return service.listarAtivos();
    }

    @PostMapping("/api/colaborador/espacos")
    public Espaco salvar(@Valid @RequestBody Espaco espaco) {
        return service.salvar(espaco);
    }

    @PutMapping("/api/colaborador/espacos/{id}")
    public Espaco atualizar(@PathVariable Long id, @Valid @RequestBody Espaco espaco) {
        return service.atualizar(id, espaco);
    }

    @PatchMapping("/api/colaborador/espacos/{id}/inativar")
    public Espaco inativar(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        return service.inativar(id, dados.get("motivo"));
    }

    @PatchMapping("/api/colaborador/espacos/{id}/reativar")
    public Espaco reativar(@PathVariable Long id) {
        return service.reativar(id);
    }

    @DeleteMapping("/api/colaborador/espacos/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
