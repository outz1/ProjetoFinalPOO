package model;

public enum SituacaoVeiculo {
    DISPONIVEL("Disponível"),
    LOCADO("Locado"),
    MANUTENCAO("Em manutenção");

    private final String rotulo;

    // TODO: guarda o rotulo exibido da situacao
    SituacaoVeiculo(String rotulo) {
        this.rotulo = rotulo;
    }

    // TODO: devolve o rotulo da situacao
    @Override
    public String toString() {
        return rotulo;
    }
}
