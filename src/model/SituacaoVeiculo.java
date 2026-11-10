package model;

public enum SituacaoVeiculo {
    DISPONIVEL("Disponível"),
    LOCADO("Locado"),
    MANUTENCAO("Em manutenção");

    private final String rotulo;

    SituacaoVeiculo(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
