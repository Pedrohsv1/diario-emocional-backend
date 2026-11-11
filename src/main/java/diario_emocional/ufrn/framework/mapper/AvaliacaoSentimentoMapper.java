package diario_emocional.ufrn.framework.mapper;

import diario_emocional.ufrn.framework.dto.AvaliacaoSentimentoRequestDTO;
import diario_emocional.ufrn.framework.dto.AvaliacaoSentimentoResponseDTO;
import diario_emocional.ufrn.framework.dto.SentimentoDTO;
import diario_emocional.ufrn.framework.entity.AvaliacaoSentimento;
import diario_emocional.ufrn.framework.entity.Sentimento;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoSentimentoMapper {
    public AvaliacaoSentimentoResponseDTO toDTO(AvaliacaoSentimento entity) {
        return new AvaliacaoSentimentoResponseDTO(
                entity.getDataRegistro(),
                entity.getAvaliacaoDia(),
                entity.getSentimentos().stream()
                        .map(s -> new SentimentoDTO(s.getSentimento(), s.getIntensidade()))
                        .toList(),
                entity.getGatilhos(),
                entity.getUsuario(),
                entity.getTextoLivre()
        );
    }

    public AvaliacaoSentimento toEntity(AvaliacaoSentimentoRequestDTO dto) {
        return new AvaliacaoSentimento(
                dto.avaliacaoDia(),
                dto.textoLivre(),
                dto.gatilhos(),
                dto.sentimentos().stream()
                        .map(s -> new Sentimento(s.sentimento(), s.intensidade()))
                        .toList()
        );
    }
}
