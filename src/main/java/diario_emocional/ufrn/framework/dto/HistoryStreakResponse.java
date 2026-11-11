package diario_emocional.ufrn.framework.dto;

import diario_emocional.ufrn.framework.entity.StreakPeriod;

import java.time.LocalDate;
import java.util.List;

public record HistoryStreakResponse(
        StreakPeriod MaiorStreak,
        StreakPeriod UltimaStreak,
        List<LocalDate> datas
) {
}
