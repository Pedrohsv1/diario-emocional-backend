package diario_emocional.ufrn.instancia_estudantes.controller;

import diario_emocional.ufrn.instancia_estudantes.entity.SugestoesEstudante;
import diario_emocional.ufrn.instancia_estudantes.service.SugestoesEstudanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sugestoes-estudante")
public class SugestoesEstudanteController {

    private final SugestoesEstudanteService sugestoesEstudanteService;

    public SugestoesEstudanteController(SugestoesEstudanteService sugestoesEstudanteService) {
        this.sugestoesEstudanteService = sugestoesEstudanteService;
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<SugestoesEstudante> gerar(@PathVariable Long usuarioId)
            throws IOException, InterruptedException {
        SugestoesEstudante sugestoes = sugestoesEstudanteService.gerarSugestoes(usuarioId, LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(sugestoes);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<SugestoesEstudante>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(sugestoesEstudanteService.listar(usuarioId));
    }

    @GetMapping("/{usuarioId}/{sugestaoId}")
    public ResponseEntity<SugestoesEstudante> buscar(
            @PathVariable Long usuarioId,
            @PathVariable Long sugestaoId
    ) {
        return ResponseEntity.ok(sugestoesEstudanteService.buscar(usuarioId, sugestaoId));
    }
}
