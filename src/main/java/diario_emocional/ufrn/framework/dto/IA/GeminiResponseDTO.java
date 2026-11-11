package diario_emocional.ufrn.framework.dto.IA;

import java.util.List;

public class GeminiResponseDTO {

    private List<CandidateDTO> candidates;

    public List<CandidateDTO> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateDTO> candidates) {
        this.candidates = candidates;
    }
}