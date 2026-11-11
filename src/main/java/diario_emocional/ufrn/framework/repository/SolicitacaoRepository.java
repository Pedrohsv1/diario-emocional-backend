package diario_emocional.ufrn.framework.repository;

import diario_emocional.ufrn.framework.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findAllByUsuario_Id(Long id);

    boolean existsByUsuario_IdAndId(Long usuarioId, Long id);

    Solicitacao findByUsuario_IdAndId(Long usuarioId, Long id);
}
