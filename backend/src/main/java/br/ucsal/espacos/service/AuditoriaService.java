package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.model.LogAuditoria;
import br.ucsal.espacos.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    @Autowired
    private LogAuditoriaRepository repository;

    public void registrar(String acao) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LogAuditoria log = new LogAuditoria();
        log.setDataHora(LocalDateTime.now());
        log.setUsuario(auth != null ? auth.getName() : "anônimo");
        log.setAcao(acao);
        repository.save(log);
    }

    public List<LogAuditoria> listar() {
        return repository.findTop200ByOrderByDataHoraDesc();
    }
}
