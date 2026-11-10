package teste;

/**
 * Executa todos os casos de teste funcionais e imprime o resultado de cada um.
 * O código de saída é 0 quando tudo passa e 1 se algum caso falhar.
 */
public final class ExecutarTestes {
    private ExecutarTestes() {
    }

    public static void main(String[] args) {
        try {
            TestesModelo.executar();
            TestesRegras.executar();
            TestesCrud.executar();
            TestesPersistencia.executar();
        } finally {
            Cenario.limpar();
        }
        Verificador.resumo();
        System.exit(Verificador.falhas() == 0 ? 0 : 1);
    }
}
