package br.ucsal.espacos.repository;

import java.util.List;

import br.ucsal.espacos.model.SolicitacaoSoftware;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoSoftwareRepository extends JpaRepository<SolicitacaoSoftware, Long> {

    List<SolicitacaoSoftware> findBySolicitanteIdOrderByDataSolicitacaoDesc(Long usuarioId);

    List<SolicitacaoSoftware> findByEspacoCentroAcademicoIdOrderByDataSolicitacaoDesc(Long centroId);

    boolean existsByEspacoId(Long espacoId);
}
