package view;

import service.Locadora;

public class TelaPrincipal extends Tela {
    private static final String VEICULOS = "Veículos";
    private static final String CLIENTES = "Clientes";
    private static final String FUNCIONARIOS = "Funcionários";
    private static final String LOCACOES = "Locações e consultas";
    private static final String ALTERACOES = "Mostrar painel de alterações";

    private final PainelAlteracoes painel;

    // TODO: cria a tela principal com o painel de alteracoes que fica aberto ao lado
    public TelaPrincipal(Locadora locadora, PainelAlteracoes painel) {
        super(locadora);
        this.painel = painel;
    }

    // TODO: retorna o titulo da tela
    @Override
    protected String titulo() {
        return "Locadora de Veículos";
    }

    // TODO: devolve as opcoes do menu
    @Override
    protected String[] opcoes() {
        return new String[]{VEICULOS, CLIENTES, FUNCIONARIOS, LOCACOES, ALTERACOES};
    }

    // TODO: troca Voltar por Sair
    @Override
    protected String rotuloVoltar() {
        return "Sair";
    }

    // TODO: abre a tela escolhida
    @Override
    protected void executar(String opcao) {
        switch (opcao) {
            case VEICULOS -> new TelaVeiculos(locadora).exibir();
            case CLIENTES -> new TelaClientes(locadora).exibir();
            case FUNCIONARIOS -> new TelaFuncionarios(locadora).exibir();
            case LOCACOES -> new TelaLocacoes(locadora).exibir();
            case ALTERACOES -> painel.abrir();
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }
}
