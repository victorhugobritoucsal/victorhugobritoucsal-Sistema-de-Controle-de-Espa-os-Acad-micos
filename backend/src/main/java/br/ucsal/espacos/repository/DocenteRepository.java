package br.ucsal.espacos.repository;

import br.ucsal.espacos.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

    boolean existsByCentroAcademicoId(Long centroId);
}
