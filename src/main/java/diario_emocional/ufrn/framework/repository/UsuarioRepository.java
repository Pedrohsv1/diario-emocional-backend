package diario_emocional.ufrn.framework.repository;

import diario_emocional.ufrn.framework.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNome(String nome);

}
