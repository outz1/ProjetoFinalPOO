package model;

public enum CategoriaAlteracao {
    VEICULOS("Veículos"),
    CLIENTES("Clientes"),
    FUNCIONARIOS("Funcionários"),
    LOCACOES("Locações");

    private final String rotulo;

    // TODO: guarda o rotulo exibido da categoria
    CategoriaAlteracao(String rotulo) {
        this.rotulo = rotulo;
    }

    // TODO: devolve o rotulo da categoria
    @Override
    public String toString() {
        return rotulo;
    }
}
