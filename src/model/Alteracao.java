package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Uma linha do historico de alteracoes guarda quando o que mudou e em qual registro Os detalhes ja
 * ficam prontos para exibir um campo por linha
 */
public class Alteracao implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime momento;
    private final CategoriaAlteracao categoria;
    private final TipoAlteracao tipo;
    private final String registro;
    private final String acao;
    private final String detalhes;

    // TODO: cria o registro de alteracao com os detalhes um por linha
    public Alteracao(LocalDateTime momento, CategoriaAlteracao categoria, TipoAlteracao tipo, String registro,
                     String acao, List<String> detalhes) {
        this.momento = momento;
        this.categoria = categoria;
        this.tipo = tipo;
        this.registro = registro;
        this.acao = acao;
        this.detalhes = String.join("\n", detalhes);
    }

    // TODO: devolve momento
    public LocalDateTime getMomento() {
        return momento;
    }

    // TODO: devolve categoria
    public CategoriaAlteracao getCategoria() {
        return categoria;
    }

    // TODO: devolve tipo
    public TipoAlteracao getTipo() {
        return tipo;
    }

    // TODO: devolve o nome curto do registro alterado
    public String getRegistro() {
        return registro;
    }

    // TODO: devolve o que foi feito no registro
    public String getAcao() {
        return acao;
    }

    // TODO: devolve os detalhes um por linha
    public String getDetalhes() {
        return detalhes;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return Formato.dataHora(momento) + " | " + categoria + " | " + tipo + " | " + registro + " | " + acao;
    }
}
