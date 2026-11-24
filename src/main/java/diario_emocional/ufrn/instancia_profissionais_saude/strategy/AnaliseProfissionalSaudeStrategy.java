package diario_emocional.ufrn.instancia_profissionais_saude.strategy;

import diario_emocional.ufrn.framework.entity.AtividadeObrigatoria;
import diario_emocional.ufrn.framework.entity.Avaliacao;
import diario_emocional.ufrn.framework.entity.RelatoDia;
import diario_emocional.ufrn.framework.entity.RelatoHigieneSono;
import diario_emocional.ufrn.framework.enums.DiaSemana;
import diario_emocional.ufrn.framework.strategy.AnaliseContextoStrategy;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
@Component
public class AnaliseProfissionalSaudeStrategy
        implements AnaliseContextoStrategy {

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

        contexto.append("ANÁLISE DA ROTINA PROFISSIONAL\n");

        for (LocalDate dia = inicio;
             !dia.isAfter(data);
             dia = dia.plusDays(1)) {

            final LocalDate diaJornada = dia;

            List<AtividadeObrigatoria> jornadas =
                    atividades.stream()
                            .filter(a -> Boolean.TRUE.equals(a.getAtivo()))
                            .filter(a -> ehJornada(a.getTitulo()))
                            .filter(a -> a.getDiasDaSemana() != null)
                            .filter(a -> a.getDiasDaSemana().stream()
                                    .anyMatch(d -> correspondeAoDia(
                                            d, diaJornada.getDayOfWeek())))
                            .toList();

            if (jornadas.isEmpty()) {
                continue;
            }

            LocalDate diaAnterior = diaJornada.minusDays(1);

            boolean houveJornadaAnterior =
                    atividades.stream()
                            .filter(a -> Boolean.TRUE.equals(a.getAtivo()))
                            .filter(a -> ehJornada(a.getTitulo()))
                            .filter(a -> a.getDiasDaSemana() != null)
                            .anyMatch(a -> a.getDiasDaSemana().stream()
                                    .anyMatch(d -> correspondeAoDia(
                                            d, diaAnterior.getDayOfWeek())));

            List<RelatoDia> relatosDoDia =
                    relatos.stream()
                            .filter(r -> r.getDataRegistro()
                                    .equals(diaJornada))
                            .toList();

            List<RelatoHigieneSono> sonosDoDia =
                    relatosSono.stream()
                            .filter(s -> s.getDataRegistro()
                                    .equals(diaJornada))
                            .toList();

            encontrou = true;

            contexto.append("\nData da jornada: ")
                    .append(diaJornada)
                    .append("\nJornadas programadas:\n");

            jornadas.forEach(a ->
                    contexto.append("- ")
                            .append(a.getTitulo())
                            .append("\n"));

            contexto.append("Existe jornada programada no dia anterior: ")
                    .append(houveJornadaAnterior ? "Sim" : "Não")
                    .append("\n");

            contexto.append("Relatos do dia:\n");

            if (relatosDoDia.isEmpty()) {
                contexto.append("Nenhum relato encontrado.\n");
            }

            relatosDoDia.forEach(r ->
                    contexto.append(r.getTitulo())
                            .append(" - ")
                            .append(r.getConteudoHtml())
                            .append("\n"));

            adicionarSono(contexto, sonosDoDia);
        }

        if (!encontrou) {
            contexto.append(
                    "Não foram encontradas jornadas profissionais "
                            + "programadas no período."
            );
        }

        return contexto.toString();
    }

    private boolean ehJornada(String titulo) {
        if (titulo == null) {
            return false;
        }

        String texto = titulo.toLowerCase(Locale.ROOT);

        return texto.contains("plantão")
                || texto.contains("plantao")
                || texto.contains("turno")
                || texto.contains("jornada")
                || texto.contains("atendimento");
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
        contexto.append("Sono associado à jornada:\n");

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