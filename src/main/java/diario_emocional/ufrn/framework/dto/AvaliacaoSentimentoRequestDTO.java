package diario_emocional.ufrn.framework.dto;

import java.util.List;

public record AvaliacaoSentimentoRequestDTO(
        Integer avaliacaoDia,
        List<SentimentoDTO> sentimentos,
        List<String> gatilhos,
        String textoLivre
) {}
