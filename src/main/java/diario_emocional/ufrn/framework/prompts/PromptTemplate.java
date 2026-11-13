package diario_emocional.ufrn.framework.prompts;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PromptTemplate {

    public final String montarPrompt() {
        return Stream.of(promptGeral(), dadosFixos(), dadosFlexiveis(), retornoJson())
                .filter(parte -> parte != null && !parte.isBlank())
                .map(String::strip)
                .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }

    protected abstract String promptGeral();

    protected String dadosFixos() {
        return "";
    }

    protected abstract String dadosFlexiveis();

    protected String retornoJson() {
        return "";
    }
}
