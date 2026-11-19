package diario_emocional.ufrn.instancia_estudantes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diario_emocional.ufrn.framework.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sugestoes_estudante")
public class SugestoesEstudante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @ElementCollection
    @CollectionTable(
            name = "itens_sugestoes_estudante",
            joinColumns = @JoinColumn(name = "sugestoes_estudante_id")
    )
    private List<ItemSugestao> sugestoes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    protected SugestoesEstudante() {
    }

    public SugestoesEstudante(LocalDate dataRegistro, Usuario usuario, List<ItemSugestao> sugestoes) {
        this.dataRegistro = dataRegistro;
        this.usuario = usuario;
        this.sugestoes = new ArrayList<>(sugestoes);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public List<ItemSugestao> getSugestoes() {
        return sugestoes;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Embeddable
    public static class ItemSugestao {

        @Column(nullable = false, length = 1000)
        private String acao;

        @Column(nullable = false, length = 2000)
        private String motivo;

        protected ItemSugestao() {
        }

        public ItemSugestao(String acao, String motivo) {
            this.acao = acao;
            this.motivo = motivo;
        }

        public String getAcao() {
            return acao;
        }

        public String getMotivo() {
            return motivo;
        }
    }
}
