package br.ucsal.espacos.repository;

import java.util.List;

import br.ucsal.espacos.model.Espaco;
import br.ucsal.espacos.model.StatusEspaco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    List<Espaco> findByCentroAcademicoId(Long centroId);

    List<Espaco> findByStatusNot(StatusEspaco status);

    boolean existsByCentroAcademicoId(Long centroId);
}
