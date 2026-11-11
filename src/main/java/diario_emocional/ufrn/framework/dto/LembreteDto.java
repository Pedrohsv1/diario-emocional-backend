package diario_emocional.ufrn.framework.dto;

import diario_emocional.ufrn.framework.enums.TipoLembrete;

import java.util.List;

public class LembreteDto {

    private Long id;

    private List<TipoLembrete> tipoLembreteList;

    public Long getId() {
        return this.id;
    }


    public List<TipoLembrete> getTipoLembreteList() {
        return this.tipoLembreteList;
    }
}