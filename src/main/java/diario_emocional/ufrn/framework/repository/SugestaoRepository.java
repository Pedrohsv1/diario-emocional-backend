package diario_emocional.ufrn.framework.repository;

import diario_emocional.ufrn.framework.entity.Sugestao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {
}
