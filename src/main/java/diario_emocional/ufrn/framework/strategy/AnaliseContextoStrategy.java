package diario_emocional.ufrn.framework.strategy;

import diario_emocional.ufrn.framework.entity.AtividadeObrigatoria;
import diario_emocional.ufrn.framework.entity.RelatoDia;
import diario_emocional.ufrn.framework.entity.RelatoHigieneSono;

import java.time.LocalDate;
import java.util.List;

import diario_emocional.ufrn.framework.entity.Avaliacao;

public interface AnaliseContextoStrategy {
    String analisar(
            List<AtividadeObrigatoria> atividades,
            List<RelatoDia> relatos,
            List<RelatoHigieneSono> relatosSono,
            List<? extends Avaliacao> avaliacoes,
            LocalDate data
    );
}