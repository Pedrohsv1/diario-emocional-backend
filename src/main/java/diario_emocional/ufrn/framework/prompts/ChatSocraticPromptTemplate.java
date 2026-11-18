package diario_emocional.ufrn.framework.prompts;

public class ChatSocraticPromptTemplate extends SocraticPromptTemplate {

    private final String tema;
    private final String contextoAdicional;
    private final String criterio;
    private final boolean solicitarTitulo;

    public ChatSocraticPromptTemplate(
            String tema,
            String historico,
            String contextoAdicional,
            String criterio,
            boolean solicitarTitulo
    ) {
        super(historico);
        this.tema = tema;
        this.contextoAdicional = contextoAdicional;
        this.criterio = criterio;
        this.solicitarTitulo = solicitarTitulo;
    }

    @Override
    protected String promptGeral() {
        String contexto = contextoAdicional == null || contextoAdicional.isBlank()
                ? ""
                : "\n\nCONTEXTO ADICIONAL:\n" + contextoAdicional;
        return """
                Ajude a pessoa a refletir sobre o tema ou problema abaixo, sem pressupor
                uma área profissional específica e sem decidir por ela:
                %s%s
                """.formatted(tema, contexto);
    }

    @Override
    protected String criterioDeConclusao() {
        return """
                CRITÉRIO DE CONCLUSÃO:
                %s
                """.formatted(criterio);
    }

    @Override
    protected String retornoJson() {
        String retorno = super.retornoJson();
        if (!solicitarTitulo) {
            return retorno;
        }

        return retorno + """

                Como esta é a primeira interação, inclua também o campo "titulo" com
                um título curto e representativo do tema.
                """;
    }
}
