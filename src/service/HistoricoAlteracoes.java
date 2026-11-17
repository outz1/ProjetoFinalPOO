package service;

import model.Alteracao;
import model.CategoriaAlteracao;
import model.TipoAlteracao;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de tudo o que foi incluido alterado ou excluido alimentada pela Locadora e lida pelo painel
 * de alteracoes Os campos de um registro sao os trechos do seu toString separados por barra vertical
 */
public class HistoricoAlteracoes {
    private static final String SEPARADOR = " \\| ";

    private ArrayList<Alteracao> registros = new ArrayList<>();
    private final List<Runnable> ouvintes = new ArrayList<>();

    // TODO: anota uma alteracao com a data e a hora de agora
    public void registrar(CategoriaAlteracao categoria, TipoAlteracao tipo, String registro, String acao,
                          List<String> detalhes) {
        registros.add(new Alteracao(LocalDateTime.now(), categoria, tipo, registro, acao, detalhes));
        avisar();
    }

    // TODO: pede para ser avisado a cada alteracao anotada ou quando o historico for recarregado
    public void adicionarOuvinte(Runnable ouvinte) {
        ouvintes.add(ouvinte);
    }

    // TODO: deixa de avisar o ouvinte
    public void removerOuvinte(Runnable ouvinte) {
        ouvintes.remove(ouvinte);
    }

    // TODO: avisa todos os ouvintes de que o historico mudou
    private void avisar() {
        for (Runnable ouvinte : new ArrayList<>(ouvintes))
            ouvinte.run();
    }

    // TODO: devolve uma copia das alteracoes da mais antiga para a mais recente
    public List<Alteracao> listar() {
        return new ArrayList<>(registros);
    }

    // TODO: lista as alteracoes de uma categoria
    public List<Alteracao> listarPorCategoria(CategoriaAlteracao categoria) {
        return registros.stream().filter(a -> a.getCategoria() == categoria).toList();
    }

    // TODO: devolve registros
    public ArrayList<Alteracao> getItens() {
        return registros;
    }

    // TODO: define registros
    public void setItens(ArrayList<Alteracao> registros) {
        this.registros = registros;
        avisar();
    }

    // TODO: separa o texto de um registro em um campo por linha
    public static List<String> campos(String texto) {
        return List.of(texto.split(SEPARADOR));
    }

    // TODO: compara o antes e o depois e devolve so o que mudou como antes seta depois
    public static List<String> diferencas(String antes, String depois) {
        List<String> a = campos(antes);
        List<String> d = campos(depois);
        List<String> mudancas = new ArrayList<>();
        if (a.size() == d.size()) {
            for (int i = 0; i < a.size(); i++) {
                if (!a.get(i).equals(d.get(i)))
                    mudancas.add(a.get(i) + "  →  " + d.get(i));
            }
            return mudancas;
        }
        // o registro ganhou ou perdeu campos como a locacao ao ser devolvida mostra o que saiu e o que entrou
        for (String campo : a) {
            if (!d.contains(campo))
                mudancas.add("- " + campo);
        }
        for (String campo : d) {
            if (!a.contains(campo))
                mudancas.add("+ " + campo);
        }
        return mudancas;
    }
}
