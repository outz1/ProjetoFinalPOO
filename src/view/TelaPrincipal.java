package view;

import service.Locadora;

public class TelaPrincipal extends Tela {
    private static final String VEICULOS = "Veículos";
    private static final String CLIENTES = "Clientes";
    private static final String FUNCIONARIOS = "Funcionários";
    private static final String LOCACOES = "Locações e consultas";

    public TelaPrincipal(Locadora locadora) {
        super(locadora);
    }

    @Override
    protected String titulo() {
        return "Locadora de Veículos";
    }

    @Override
    protected String[] opcoes() {
        return new String[]{VEICULOS, CLIENTES, FUNCIONARIOS, LOCACOES};
    }

    @Override
    protected String rotuloVoltar() {
        return "Sair";
    }

    @Override
    protected void executar(String opcao) {
        switch (opcao) {
            case VEICULOS -> new TelaVeiculos(locadora).exibir();
            case CLIENTES -> new TelaClientes(locadora).exibir();
            case FUNCIONARIOS -> new TelaFuncionarios(locadora).exibir();
            case LOCACOES -> new TelaLocacoes(locadora).exibir();
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }
}
