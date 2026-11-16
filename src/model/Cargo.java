package model;

public enum Cargo {
    ATENDENTE("Atendente"),
    GERENTE("Gerente");

    private final String rotulo;

    // TODO: guarda o rotulo exibido do cargo
    Cargo(String rotulo) {
        this.rotulo = rotulo;
    }

    // TODO: devolve o rotulo do cargo
    @Override
    public String toString() {
        return rotulo;
    }
}
