package br.ucsal.espacos.repository;

import br.ucsal.espacos.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    boolean existsByCentroAcademicoId(Long centroId);
}
