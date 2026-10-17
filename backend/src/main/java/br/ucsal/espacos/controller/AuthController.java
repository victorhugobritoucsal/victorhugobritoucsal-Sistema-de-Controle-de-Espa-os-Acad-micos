package br.ucsal.espacos.controller;

import java.util.Map;

import br.ucsal.espacos.model.Usuario;
import br.ucsal.espacos.service.AuditoriaService;
import br.ucsal.espacos.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoria;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> dados, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dados.get("login"), dados.get("senha")));
            SecurityContext contexto = SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(auth);
            SecurityContextHolder.setContext(contexto);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, contexto);
        } catch (AuthenticationException e) {
            auditoria.registrar("Tentativa de login sem sucesso: " + dados.get("login"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensagem", "Login ou senha inválidos"));
        }
        auditoria.registrar("Fez login");
        return ResponseEntity.ok(usuarioService.usuarioLogado());
    }

    @GetMapping("/me")
    public Usuario me() {
        return usuarioService.usuarioLogado();
    }
}
