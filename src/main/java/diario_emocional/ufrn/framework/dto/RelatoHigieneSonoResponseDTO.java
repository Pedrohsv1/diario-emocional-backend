package diario_emocional.ufrn.framework.dto;

import diario_emocional.ufrn.framework.enums.NivelDisposicao;
import diario_emocional.ufrn.framework.enums.QualidadeSono;

import java.time.LocalDate;
import java.time.LocalTime;

public record RelatoHigieneSonoResponseDTO(
        Long id,
        LocalDate dataRegistro,
        LocalTime horaDormir,
        LocalTime horaAcordar,
        long duracaoSonoMinutos,
        QualidadeSono qualidadeSono,
        boolean usouCelular,
        boolean tevePesadelos,
        String comentarioSonhos,
        NivelDisposicao nivelDisposicao
) {
}