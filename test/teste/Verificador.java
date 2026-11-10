package teste;

import java.util.Objects;

/** Registra cada caso de teste, confere resultados e imprime o resumo. */
public final class Verificador {
    @FunctionalInterface
    public interface Teste {
        void executar() throws Exception;
    }

    private static int total = 0;
    private static int falhas = 0;

    private Verificador() {
    }

    static void grupo(String nome) {
        System.out.println("\n" + nome);
    }

    /** Executa um caso de teste. O id e os requisitos mostram o que ele verifica. */
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

    static void igual(Object esperado, Object atual) {
        if (!Objects.equals(esperado, atual))
            throw new AssertionError("esperado <" + esperado + "> mas foi <" + atual + ">");
    }

    static void igual(double esperado, double atual) {
        if (Math.abs(esperado - atual) > 0.001)
            throw new AssertionError("esperado <" + esperado + "> mas foi <" + atual + ">");
    }

    static void verdadeiro(boolean condicao, String mensagem) {
        if (!condicao)
            throw new AssertionError(mensagem);
    }

    /** Confere que o código lança a exceção esperada (ou uma subclasse) e devolve a exceção para novas conferências. */
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

    /** Como lanca, mas exige exatamente a classe pedida (não aceita subclasse). */
    static <E extends Throwable> E lancaExata(Class<E> tipo, Teste codigo) {
        E erro = lanca(tipo, codigo);
        if (erro.getClass() != tipo)
            throw new AssertionError("esperava exatamente " + tipo.getSimpleName() + " mas veio " + erro.getClass().getSimpleName());
        return erro;
    }

    static int falhas() {
        return falhas;
    }

    static void resumo() {
        System.out.println("\n" + total + " casos executados, " + (total - falhas) + " passaram, " + falhas + " falharam.");
    }
}
