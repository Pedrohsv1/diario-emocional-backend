package diario_emocional.ufrn.framework.controller;

import diario_emocional.ufrn.framework.dto.LembreteResponseDto;
import diario_emocional.ufrn.framework.service.LembreteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lembrete")
public class LembreteController {

    private final LembreteService lembreteService;

    public LembreteController(LembreteService lembreteService) {
        this.lembreteService = lembreteService;
    }

    @GetMapping("/nao-enviados")
    public ResponseEntity<LembreteResponseDto> buscarNaoEnviados() {

        LembreteResponseDto lembrete = lembreteService.verificarLembreteParaEnviar();

        if (lembrete == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lembrete);
    }
}