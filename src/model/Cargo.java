package model;

public enum Cargo {
    ATENDENTE("Atendente"),
    GERENTE("Gerente");

    private final String rotulo;

    Cargo(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
