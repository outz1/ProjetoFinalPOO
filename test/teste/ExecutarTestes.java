package teste;

/**
 * Executa todos os casos de teste funcionais e imprime o resultado de cada um
 * O codigo de saida e 0 quando tudo passa e 1 se algum caso falhar
 */
public final class ExecutarTestes {
    // TODO: classe utilitaria nao instanciavel
    private ExecutarTestes() {
    }

    // TODO: roda todos os grupos de teste e sai com 0 se tudo passou
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
