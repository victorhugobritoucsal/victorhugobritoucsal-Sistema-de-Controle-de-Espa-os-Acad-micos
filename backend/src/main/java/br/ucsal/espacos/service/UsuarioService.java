package br.ucsal.espacos.service;

import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.CentroAcademico;
import br.ucsal.espacos.model.Perfil;
import br.ucsal.espacos.model.Usuario;
import br.ucsal.espacos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditoriaService auditoria;

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
    }

    public Usuario salvar(Usuario usuario) {
        validar(usuario);
        validarSenha(usuario.getSenha());
        usuario.setId(null);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);
        Usuario salvo = repository.save(usuario);
        auditoria.registrar("Cadastrou o usuário " + salvo.getLogin());
        return salvo;
    }

    public Usuario atualizar(Long id, Usuario dados) {
        Usuario usuario = buscar(id);
        validar(dados);
        usuario.setNome(dados.getNome());
        usuario.setDocente(dados.getDocente());
        usuario.setPerfis(dados.getPerfis());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            validarSenha(dados.getSenha());
            usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        }
        Usuario salvo = repository.save(usuario);
        auditoria.registrar("Editou o usuário " + salvo.getLogin());
        return salvo;
    }

    public Usuario alterarStatus(Long id, boolean ativo) {
        Usuario usuario = buscar(id);
        usuario.setAtivo(ativo);
        auditoria.registrar((ativo ? "Reativou" : "Inativou") + " o usuário " + usuario.getLogin());
        return repository.save(usuario);
    }

    public Usuario usuarioLogado() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByLogin(login).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
    }

    public CentroAcademico centroDoUsuarioLogado() {
        Usuario usuario = usuarioLogado();
        if (usuario.getDocente() == null) {
            throw new RegraException("Seu usuário não está ligado a um docente");
        }
        return usuario.getDocente().getCentroAcademico();
    }

    private void validar(Usuario usuario) {
        if (usuario.getPerfis() == null || usuario.getPerfis().isEmpty()) {
            throw new RegraException("Escolha pelo menos um perfil");
        }
        boolean precisaDeDocente = usuario.getPerfis().contains(Perfil.COLABORADOR)
                || usuario.getPerfis().contains(Perfil.USUARIO);
        if (precisaDeDocente && usuario.getDocente() == null) {
            throw new RegraException("Os perfis Colaborador e Usuário precisam estar ligados a um docente");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new RegraException("A senha deve ter pelo menos 6 caracteres");
        }
        if (senha.getBytes().length > 72) {
            throw new RegraException("A senha é muito longa");
        }
    }
}
