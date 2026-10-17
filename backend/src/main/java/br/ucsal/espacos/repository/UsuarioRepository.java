package br.ucsal.espacos.repository;

import java.util.Optional;

import br.ucsal.espacos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);
}
