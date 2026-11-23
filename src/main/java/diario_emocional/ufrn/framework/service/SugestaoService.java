package diario_emocional.ufrn.framework.service;

import diario_emocional.ufrn.framework.dto.RespostasSugestaoDTO;
import diario_emocional.ufrn.framework.dto.SugestaoDTO;
import diario_emocional.ufrn.framework.dto.IA.GeminiResponseDTO;
import diario_emocional.ufrn.framework.entity.Solicitacao;
import diario_emocional.ufrn.framework.entity.Sugestao;
import diario_emocional.ufrn.framework.entity.Usuario;
import diario_emocional.ufrn.framework.exception.ResourceNotFoundException;
import diario_emocional.ufrn.framework.repository.SolicitacaoRepository;
import diario_emocional.ufrn.framework.repository.UsuarioRepository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class SugestaoService {

    protected final IAService iaService;
    protected final ObjectMapper objectMapper;

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;

    protected SugestaoService(
            IAService iaService,
            ObjectMapper objectMapper,
            SolicitacaoRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.iaService = iaService;
        this.objectMapper = objectMapper;
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    protected abstract String criarPromptComContexto(Long usuarioId, LocalDate data);

    protected void validarSugestoes(List<SugestaoDTO> sugestoes) {
    }

    public Solicitacao criarSolicitacao(Long usuarioId, LocalDate data)
            throws IOException, InterruptedException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "O usuário responsável não existe."
                ));

        String prompt = criarPromptComContexto(usuarioId, data);
        GeminiResponseDTO respostaIA = iaService.retornarSugestoesSaudeMental(prompt);
        String respostaJson = iaService.retornarStringSugestao(respostaIA);
        RespostasSugestaoDTO resposta =
                objectMapper.readValue(respostaJson, RespostasSugestaoDTO.class);

        if (resposta == null || resposta.getSugestoes() == null) {
            throw new IllegalStateException(
                    "A IA não retornou uma lista válida de sugestões."
            );
        }

        List<SugestaoDTO> sugestoesDTO = resposta.getSugestoes();
        validarSugestoes(sugestoesDTO);

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setDataRegistro(data);
        solicitacao.setUsuario(usuario);

        List<Sugestao> sugestoes = new ArrayList<>();
        for (SugestaoDTO sugestaoDTO : sugestoesDTO) {
            Sugestao sugestao = new Sugestao(
                    sugestaoDTO.getAcao(),
                    sugestaoDTO.getMotivo()
            );
            sugestao.setSolicitacao(solicitacao);
            sugestoes.add(sugestao);
        }

        solicitacao.setSugestoes(sugestoes);
        return solicitacaoRepository.save(solicitacao);
    }

    public List<Solicitacao> buscarVariasSolicitacoes(Long usuarioId) {
        validarUsuario(usuarioId);
        return solicitacaoRepository
                .findAllByUsuario_IdOrderByDataRegistroDescIdDesc(usuarioId);
    }

    public Solicitacao buscarSolicitacaoEspecifica(Long usuarioId, Long solicitacaoId) {
        validarUsuario(usuarioId);

        if (!solicitacaoRepository.existsByUsuario_IdAndId(usuarioId, solicitacaoId)) {
            throw new ResourceNotFoundException(
                    "Não foi possível encontrar essa solicitação de saúde mental para esse usuário."
            );
        }

        return solicitacaoRepository.findByUsuario_IdAndId(usuarioId, solicitacaoId);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "O usuário responsável não existe."
            );
        }
    }
}
