package diario_emocional.ufrn.instancia_estudantes.service;

import diario_emocional.ufrn.framework.dto.IA.GeminiResponseDTO;
import diario_emocional.ufrn.framework.dto.RespostasSugestaoDTO;
import diario_emocional.ufrn.framework.dto.SugestaoDTO;
import diario_emocional.ufrn.framework.entity.AtividadeObrigatoria;
import diario_emocional.ufrn.framework.entity.RelatoDia;
import diario_emocional.ufrn.framework.entity.RelatoHigieneSono;
import diario_emocional.ufrn.framework.entity.Usuario;
import diario_emocional.ufrn.framework.exception.ResourceNotFoundException;
import diario_emocional.ufrn.framework.prompts.PromptTemplate;
import diario_emocional.ufrn.framework.repository.AtividadeObrigatoriaRepository;
import diario_emocional.ufrn.framework.repository.RelatoDiaRepository;
import diario_emocional.ufrn.framework.repository.RelatoHigieneSonoRepository;
import diario_emocional.ufrn.framework.repository.UsuarioRepository;
import diario_emocional.ufrn.framework.service.IAService;
import diario_emocional.ufrn.instancia_estudantes.entity.AvaliacaoEstudante;
import diario_emocional.ufrn.instancia_estudantes.entity.SugestoesEstudante;
import diario_emocional.ufrn.instancia_estudantes.prompts.SugestoesEstudantePromptTemplate;
import diario_emocional.ufrn.instancia_estudantes.repository.AvaliacaoEstudanteRepository;
import diario_emocional.ufrn.instancia_estudantes.repository.SugestoesEstudanteRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SugestoesEstudanteService {

    private final IAService iaService;
    private final ObjectMapper objectMapper;
    private final SugestoesEstudanteRepository sugestoesEstudanteRepository;
    private final RelatoDiaRepository relatoDiaRepository;
    private final RelatoHigieneSonoRepository relatoHigieneSonoRepository;
    private final AtividadeObrigatoriaRepository atividadeObrigatoriaRepository;
    private final AvaliacaoEstudanteRepository avaliacaoEstudanteRepository;
    private final UsuarioRepository usuarioRepository;

    public SugestoesEstudanteService(
            IAService iaService,
            ObjectMapper objectMapper,
            SugestoesEstudanteRepository sugestoesEstudanteRepository,
            RelatoDiaRepository relatoDiaRepository,
            RelatoHigieneSonoRepository relatoHigieneSonoRepository,
            AtividadeObrigatoriaRepository atividadeObrigatoriaRepository,
            AvaliacaoEstudanteRepository avaliacaoEstudanteRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.iaService = iaService;
        this.objectMapper = objectMapper;
        this.sugestoesEstudanteRepository = sugestoesEstudanteRepository;
        this.relatoDiaRepository = relatoDiaRepository;
        this.relatoHigieneSonoRepository = relatoHigieneSonoRepository;
        this.atividadeObrigatoriaRepository = atividadeObrigatoriaRepository;
        this.avaliacaoEstudanteRepository = avaliacaoEstudanteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SugestoesEstudante gerarSugestoes(Long usuarioId, LocalDate data)
            throws IOException, InterruptedException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("O usuário responsável não existe."));

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
                relatos, relatosSono, atividades, avaliacoes, objectMapper
        );
        GeminiResponseDTO respostaIA = iaService.retornarSugestoesSaudeMental(template.montarPrompt());
        String resposta = iaService.retornarStringSugestao(respostaIA);
        RespostasSugestaoDTO respostas = objectMapper.readValue(resposta, RespostasSugestaoDTO.class);

        if (respostas == null || respostas.getSugestoes() == null || respostas.getSugestoes().size() != 3) {
            throw new IllegalStateException("A IA não retornou exatamente 3 sugestões para o estudante.");
        }

        List<SugestoesEstudante.ItemSugestao> sugestoes = new ArrayList<>();
        for (SugestaoDTO sugestao : respostas.getSugestoes()) {
            sugestoes.add(new SugestoesEstudante.ItemSugestao(
                    sugestao.getAcao(),
                    sugestao.getMotivo()
            ));
        }

        SugestoesEstudante geracao = new SugestoesEstudante(data, usuario, sugestoes);
        return sugestoesEstudanteRepository.save(geracao);
    }

    public List<SugestoesEstudante> listar(Long usuarioId) {
        validarUsuario(usuarioId);
        return sugestoesEstudanteRepository.findAllByUsuario_IdOrderByDataRegistroDescIdDesc(usuarioId);
    }

    public SugestoesEstudante buscar(Long usuarioId, Long sugestaoId) {
        validarUsuario(usuarioId);
        return sugestoesEstudanteRepository.findByUsuario_IdAndId(usuarioId, sugestaoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Não foi possível encontrar essa sugestão para esse usuário."
                ));
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

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("O usuário responsável não existe.");
        }
    }
}
