package diario_emocional.ufrn.framework.mapper;

import diario_emocional.ufrn.framework.dto.IA.ChatResponseDTO;
import diario_emocional.ufrn.framework.entity.Chat;
import diario_emocional.ufrn.framework.entity.Mensagem;
import diario_emocional.ufrn.framework.enums.MensagemRole;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    private final MensagemMapper mensagemMapper;

    public ChatMapper(MensagemMapper mensagemMapper) {
        this.mensagemMapper = mensagemMapper;
    }

    public ChatResponseDTO toDTO(Chat chat) {
        if (chat == null) {
            return null;
        }

        String pergunta = chat.getMensagems().stream()
                .filter(mensagem -> mensagem.getPapel() == MensagemRole.IA)
                .map(Mensagem::getConteudo)
                .reduce((anterior, atual) -> atual)
                .orElse(null);

        return new ChatResponseDTO(
                chat.getId(),
                chat.getTitulo(),
                chat.getModelo(),
                mensagemMapper.toDTOList(chat.getMensagems()),
                pergunta,
                Boolean.TRUE.equals(chat.getConcluiu())
        );
    }
}