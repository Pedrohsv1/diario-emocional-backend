package diario_emocional.ufrn.instancia_estudantes.repository;

import diario_emocional.ufrn.instancia_estudantes.entity.SugestoesEstudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SugestoesEstudanteRepository extends JpaRepository<SugestoesEstudante, Long> {

    List<SugestoesEstudante> findAllByUsuario_IdOrderByDataRegistroDescIdDesc(Long usuarioId);

    Optional<SugestoesEstudante> findByUsuario_IdAndId(Long usuarioId, Long id);
}
