package diario_emocional.ufrn.instancia_estudantes.service;

import diario_emocional.ufrn.framework.dto.SugestaoDTO;
import diario_emocional.ufrn.framework.entity.AtividadeObrigatoria;
import diario_emocional.ufrn.framework.entity.RelatoDia;
import diario_emocional.ufrn.framework.entity.RelatoHigieneSono;
import diario_emocional.ufrn.framework.exception.ResourceNotFoundException;
import diario_emocional.ufrn.framework.prompts.PromptTemplate;
import diario_emocional.ufrn.framework.repository.AtividadeObrigatoriaRepository;
import diario_emocional.ufrn.framework.repository.RelatoDiaRepository;
import diario_emocional.ufrn.framework.repository.RelatoHigieneSonoRepository;
import diario_emocional.ufrn.framework.repository.SolicitacaoRepository;
import diario_emocional.ufrn.framework.repository.UsuarioRepository;
import diario_emocional.ufrn.framework.service.IAService;
import diario_emocional.ufrn.framework.service.SugestaoService;
import diario_emocional.ufrn.instancia_estudantes.entity.AvaliacaoEstudante;
import diario_emocional.ufrn.instancia_estudantes.prompts.SugestoesEstudantePromptTemplate;
import diario_emocional.ufrn.instancia_estudantes.repository.AvaliacaoEstudanteRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SugestoesEstudanteService extends SugestaoService {

    private final RelatoDiaRepository relatoDiaRepository;
    private final RelatoHigieneSonoRepository relatoHigieneSonoRepository;
    private final AtividadeObrigatoriaRepository atividadeObrigatoriaRepository;
    private final AvaliacaoEstudanteRepository avaliacaoEstudanteRepository;

    public SugestoesEstudanteService(
            IAService iaService,
            ObjectMapper objectMapper,
            SolicitacaoRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository,
            RelatoDiaRepository relatoDiaRepository,
            RelatoHigieneSonoRepository relatoHigieneSonoRepository,
            AtividadeObrigatoriaRepository atividadeObrigatoriaRepository,
            AvaliacaoEstudanteRepository avaliacaoEstudanteRepository
    ) {
        super(iaService, objectMapper, solicitacaoRepository, usuarioRepository);
        this.relatoDiaRepository = relatoDiaRepository;
        this.relatoHigieneSonoRepository = relatoHigieneSonoRepository;
        this.atividadeObrigatoriaRepository = atividadeObrigatoriaRepository;
        this.avaliacaoEstudanteRepository = avaliacaoEstudanteRepository;
    }

    @Override
    protected String criarPromptComContexto(Long usuarioId, LocalDate data) {
        LocalDate dataInicial = data.minusDays(6);

        List<RelatoDia> relatos = relatoDiaRepository
                .findAllByUsuarioIdAndDataRegistroBetween(usuarioId, dataInicial, data);

        List<RelatoHigieneSono> relatosSono = relatoHigieneSonoRepository
                .findAllByUsuario_IdAndDataRegistroBetween(usuarioId, dataInicial, data);

        List<AtividadeObrigatoria> atividades = atividadeObrigatoriaRepository
                .findAllByUsuario_IdAndAtivoTrue(usuarioId);

        List<AvaliacaoEstudante> avaliacoes = avaliacaoEstudanteRepository
                .findAllByUsuario_IdAndDataRegistroBetween(usuarioId, dataInicial, data);

        validarContexto(relatos, relatosSono, atividades, avaliacoes);

        PromptTemplate template = new SugestoesEstudantePromptTemplate(
                relatos,
                relatosSono,
                atividades,
                avaliacoes,
                objectMapper
        );

        return template.montarPrompt();
    }

    @Override
    protected void validarSugestoes(List<SugestaoDTO> sugestoes) {
        if (sugestoes.size() != 3) {
            throw new IllegalStateException(
                    "A IA não retornou exatamente 3 sugestões para o estudante."
            );
        }
    }

    private void validarContexto(
            List<RelatoDia> relatos,
            List<RelatoHigieneSono> relatosSono,
            List<AtividadeObrigatoria> atividades,
            List<AvaliacaoEstudante> avaliacoes
    ) {
        List<String> erros = new ArrayList<>();

        if (relatos.isEmpty()) {
            erros.add("Não existe nenhum relato do dia nos últimos 7 dias.");
        }

        if (relatosSono.isEmpty()) {
            erros.add("Não existe nenhum relato de higiene do sono nos últimos 7 dias.");
        }

        if (atividades.isEmpty()) {
            erros.add("Não existe nenhuma atividade obrigatória cadastrada.");
        }

        if (avaliacoes.isEmpty()) {
            erros.add("Não existe nenhuma avaliação de sentimentos nos últimos 7 dias.");
        }

        if (!erros.isEmpty()) {
            throw new ResourceNotFoundException(String.join(" ", erros));
        }
    }
}