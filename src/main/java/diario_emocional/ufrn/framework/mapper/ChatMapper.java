package diario_emocional.ufrn.framework.mapper;

import diario_emocional.ufrn.framework.dto.IA.ChatResponseDTO;
import diario_emocional.ufrn.framework.entity.Chat;
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

        return new ChatResponseDTO(
                chat.getId(),
                chat.getTitulo(),
                chat.getModelo(),
                mensagemMapper.toDTOList(chat.getMensagems())
        );
    }
}