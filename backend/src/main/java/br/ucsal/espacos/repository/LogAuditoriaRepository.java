package br.ucsal.espacos.repository;

import java.util.List;

import br.ucsal.espacos.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findTop200ByOrderByDataHoraDesc();
}
