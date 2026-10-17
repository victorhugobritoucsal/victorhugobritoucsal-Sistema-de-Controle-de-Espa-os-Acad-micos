package br.ucsal.espacos.controller;

import java.util.List;

import br.ucsal.espacos.model.Usuario;
import br.ucsal.espacos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @PostMapping
    public Usuario salvar(@Valid @RequestBody Usuario usuario) {
        return service.salvar(usuario);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return service.atualizar(id, usuario);
    }

    @PatchMapping("/{id}/inativar")
    public Usuario inativar(@PathVariable Long id) {
        return service.alterarStatus(id, false);
    }

    @PatchMapping("/{id}/reativar")
    public Usuario reativar(@PathVariable Long id) {
        return service.alterarStatus(id, true);
    }
}
