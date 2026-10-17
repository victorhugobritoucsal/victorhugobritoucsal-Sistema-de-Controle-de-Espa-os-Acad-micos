package br.ucsal.espacos.repository;

import br.ucsal.espacos.model.CentroAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentroAcademicoRepository extends JpaRepository<CentroAcademico, Long> {

    boolean existsByIesId(Long iesId);

    boolean existsByCoordenadorIdAndIdNot(Long docenteId, Long id);
}
