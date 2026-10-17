package br.ucsal.espacos.repository;

import java.time.LocalDate;
import java.util.List;

import br.ucsal.espacos.model.SolicitacaoEspaco;
import br.ucsal.espacos.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoEspacoRepository extends JpaRepository<SolicitacaoEspaco, Long> {

    List<SolicitacaoEspaco> findBySolicitanteIdOrderByDataSolicitacaoDesc(Long usuarioId);

    List<SolicitacaoEspaco> findByEspacoCentroAcademicoIdOrderByDataSolicitacaoDesc(Long centroId);

    List<SolicitacaoEspaco> findByEspacoIdAndDataAndStatus(Long espacoId, LocalDate data, StatusSolicitacao status);

    boolean existsByEspacoIdAndStatus(Long espacoId, StatusSolicitacao status);

    boolean existsByEspacoId(Long espacoId);
}
