package model;

public enum SituacaoLocacao {
    ATIVA("Ativa"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String rotulo;

    SituacaoLocacao(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
