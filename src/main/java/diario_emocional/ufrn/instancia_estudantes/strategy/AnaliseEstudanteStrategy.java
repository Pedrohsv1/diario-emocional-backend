package diario_emocional.ufrn.instancia_estudantes.strategy;

import diario_emocional.ufrn.framework.entity.AtividadeObrigatoria;
import diario_emocional.ufrn.framework.entity.Avaliacao;
import diario_emocional.ufrn.framework.entity.RelatoDia;
import diario_emocional.ufrn.framework.entity.RelatoHigieneSono;
import diario_emocional.ufrn.framework.enums.DiaSemana;
import diario_emocional.ufrn.framework.strategy.AnaliseContextoStrategy;
import diario_emocional.ufrn.instancia_estudantes.entity.AvaliacaoEstudante;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
@Component

public class AnaliseEstudanteStrategy implements AnaliseContextoStrategy {

    @Override
    public String analisar(
            List<AtividadeObrigatoria> atividades,
            List<RelatoDia> relatos,
            List<RelatoHigieneSono> relatosSono,
            List<? extends Avaliacao> avaliacoes,
            LocalDate data
    ) {
        StringBuilder contexto = new StringBuilder();
        boolean encontrou = false;

        LocalDate inicio = data.minusDays(6);

        contexto.append("ANÁLISE DA ROTINA ACADÊMICA\n");

        for (LocalDate dia = inicio;
             !dia.isAfter(data);
             dia = dia.plusDays(1)) {

            final LocalDate diaAtividade = dia;

            List<AtividadeObrigatoria> atividadesAcademicas =
                    atividades.stream()
                            .filter(a -> Boolean.TRUE.equals(a.getAtivo()))
                            .filter(a -> ehAtividadeAcademica(a.getTitulo()))
                            .filter(a -> a.getDiasDaSemana() != null)
                            .filter(a -> a.getDiasDaSemana().stream()
                                    .anyMatch(d -> correspondeAoDia(
                                            d, diaAtividade.getDayOfWeek())))
                            .toList();

            if (atividadesAcademicas.isEmpty()) {
                continue;
            }

            LocalDate diaAnterior = diaAtividade.minusDays(1);

            List<RelatoDia> relatosRelacionados =
                    relatos.stream()
                            .filter(r ->
                                    r.getDataRegistro().equals(diaAnterior)
                                            || r.getDataRegistro().equals(diaAtividade))
                            .toList();

            List<AvaliacaoEstudante> avaliacoesRelacionadas =
                    avaliacoes.stream()
                            .filter(AvaliacaoEstudante.class::isInstance)
                            .map(AvaliacaoEstudante.class::cast)
                            .filter(a -> a.getDataRegistro()
                                    .equals(diaAtividade))
                            .toList();

            List<RelatoHigieneSono> sonoRelacionado =
                    relatosSono.stream()
                            .filter(s -> s.getDataRegistro()
                                    .equals(diaAtividade))
                            .toList();

            encontrou = true;

            contexto.append("\nData da atividade: ")
                    .append(diaAtividade)
                    .append("\nAtividades acadêmicas:\n");

            atividadesAcademicas.forEach(a ->
                    contexto.append("- ")
                            .append(a.getTitulo())
                            .append("\n"));

            contexto.append("Relatos anteriores e do dia:\n");

            if (relatosRelacionados.isEmpty()) {
                contexto.append("Nenhum relato encontrado.\n");
            }

            relatosRelacionados.forEach(r ->
                    contexto.append("Data: ")
                            .append(r.getDataRegistro())
                            .append("\nTítulo: ")
                            .append(r.getTitulo())
                            .append("\nConteúdo: ")
                            .append(r.getConteudoHtml())
                            .append("\n"));

            contexto.append("Avaliações emocionais:\n");

            if (avaliacoesRelacionadas.isEmpty()) {
                contexto.append("Nenhuma avaliação encontrada.\n");
            }

            avaliacoesRelacionadas.forEach(a ->
                    contexto.append("Sentimentos: ")
                            .append(a.getSentimentos())
                            .append("\nGatilhos: ")
                            .append(a.getGatilhos())
                            .append("\nTexto livre: ")
                            .append(a.getTextoLivre())
                            .append("\n"));

            adicionarSono(contexto, sonoRelacionado);
        }

        if (!encontrou) {
            contexto.append(
                    "Não foram encontradas atividades acadêmicas "
                            + "programadas no período."
            );
        }

        return contexto.toString();
    }

    private boolean ehAtividadeAcademica(String titulo) {
        if (titulo == null) {
            return false;
        }

        String texto = titulo.toLowerCase(Locale.ROOT);

        return texto.contains("prova")
                || texto.contains("trabalho")
                || texto.contains("seminário")
                || texto.contains("seminario")
                || texto.contains("entrega")
                || texto.contains("apresentação")
                || texto.contains("apresentacao");
    }

    private boolean correspondeAoDia(
            DiaSemana dia,
            DayOfWeek diaSemana
    ) {
        return switch (diaSemana) {
            case MONDAY -> dia == DiaSemana.SEGUNDA;
            case TUESDAY -> dia == DiaSemana.TERCA;
            case WEDNESDAY -> dia == DiaSemana.QUARTA;
            case THURSDAY -> dia == DiaSemana.QUINTA;
            case FRIDAY -> dia == DiaSemana.SEXTA;
            case SATURDAY -> dia == DiaSemana.SABADO;
            case SUNDAY -> dia == DiaSemana.DOMINGO;
        };
    }

    private void adicionarSono(
            StringBuilder contexto,
            List<RelatoHigieneSono> sonos
    ) {
        contexto.append("Sono associado à atividade:\n");

        if (sonos.isEmpty()) {
            contexto.append("Nenhum registro de sono encontrado.\n");
            return;
        }

        sonos.forEach(s ->
                contexto.append("Duração: ")
                        .append(s.getDuracaoSonoMinutos() / 60.0)
                        .append(" horas\nQualidade: ")
                        .append(s.getQualidadeSono())
                        .append("\nDisposição: ")
                        .append(s.getNivelDisposicao())
                        .append("\n"));
    }
}