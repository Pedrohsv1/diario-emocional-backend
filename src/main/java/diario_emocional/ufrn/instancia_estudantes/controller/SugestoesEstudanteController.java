package diario_emocional.ufrn.instancia_estudantes.controller;

import diario_emocional.ufrn.framework.entity.Solicitacao;
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
    public ResponseEntity<Solicitacao> gerar(@PathVariable Long usuarioId)
            throws IOException, InterruptedException {
        Solicitacao solicitacao =
                sugestoesEstudanteService.criarSolicitacao(usuarioId, LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacao);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Solicitacao>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(sugestoesEstudanteService.buscarVariasSolicitacoes(usuarioId));
    }

    @GetMapping("/{usuarioId}/{sugestaoId}")
    public ResponseEntity<Solicitacao> buscar(
            @PathVariable Long usuarioId,
            @PathVariable Long sugestaoId
    ) {
        return ResponseEntity.ok(
                sugestoesEstudanteService.buscarSolicitacaoEspecifica(usuarioId, sugestaoId)
        );
    }
}
