package diario_emocional.ufrn.framework.repository;

import diario_emocional.ufrn.framework.entity.Lembrete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    void deleteByAtividadeObrigatoriaId(Long atividadeId);
    }
