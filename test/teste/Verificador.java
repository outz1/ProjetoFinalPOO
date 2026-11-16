package teste;

import java.util.Objects;

/** Registra cada caso de teste confere resultados e imprime o resumo */
public final class Verificador {
    @FunctionalInterface
    public interface Teste {
        // TODO: roda o corpo do teste
        void executar() throws Exception;
    }

    private static int total = 0;
    private static int falhas = 0;

    // TODO: classe utilitaria nao instanciavel
    private Verificador() {
    }

    // TODO: imprime o titulo de um grupo de casos
    static void grupo(String nome) {
        System.out.println("\n" + nome);
    }

    // TODO: executa um caso de teste e registra se passou ou falhou
    static void caso(String id, String requisitos, String descricao, Teste teste) {
        total++;
        try {
            teste.executar();
            System.out.println("  [OK]    " + id + " (" + requisitos + ") " + descricao);
        } catch (Throwable e) {
            falhas++;
            System.out.println("  [FALHA] " + id + " (" + requisitos + ") " + descricao + "\n          " + e);
        }
    }

    // TODO: confere que o valor atual e igual ao esperado
    static void igual(Object esperado, Object atual) {
        if (!Objects.equals(esperado, atual))
            throw new AssertionError("esperado <" + esperado + "> mas foi <" + atual + ">");
    }

    // TODO: confere que o valor atual e igual ao esperado
    static void igual(double esperado, double atual) {
        if (Math.abs(esperado - atual) > 0.001)
            throw new AssertionError("esperado <" + esperado + "> mas foi <" + atual + ">");
    }

    // TODO: confere que a condicao e verdadeira
    static void verdadeiro(boolean condicao, String mensagem) {
        if (!condicao)
            throw new AssertionError(mensagem);
    }

    // TODO: confere que o codigo lanca a excecao esperada ou subclasse e a devolve
    static <E extends Throwable> E lanca(Class<E> tipo, Teste codigo) {
        try {
            codigo.executar();
        } catch (Throwable e) {
            if (tipo.isInstance(e))
                return tipo.cast(e);
            throw new AssertionError("esperava " + tipo.getSimpleName() + " mas veio " + e, e);
        }
        throw new AssertionError("esperava " + tipo.getSimpleName() + " mas nada foi lançado");
    }

    // TODO: como lanca mas exige exatamente a classe pedida
    static <E extends Throwable> E lancaExata(Class<E> tipo, Teste codigo) {
        E erro = lanca(tipo, codigo);
        if (erro.getClass() != tipo)
            throw new AssertionError("esperava exatamente " + tipo.getSimpleName() + " mas veio " + erro.getClass().getSimpleName());
        return erro;
    }

    // TODO: devolve a quantidade de casos que falharam
    static int falhas() {
        return falhas;
    }

    // TODO: imprime o total de casos aprovados e reprovados
    static void resumo() {
        System.out.println("\n" + total + " casos executados, " + (total - falhas) + " passaram, " + falhas + " falharam.");
    }
}
