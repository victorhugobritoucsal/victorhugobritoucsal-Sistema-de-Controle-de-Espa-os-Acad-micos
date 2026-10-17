package br.ucsal.espacos.repository;

import java.util.List;

import br.ucsal.espacos.model.HistoricoDocente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoDocenteRepository extends JpaRepository<HistoricoDocente, Long> {

    List<HistoricoDocente> findByDocenteIdOrderByDataDesc(Long docenteId);
}
