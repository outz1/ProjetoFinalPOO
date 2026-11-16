package model;

public enum SituacaoLocacao {
    ATIVA("Ativa"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String rotulo;

    // TODO: guarda o rotulo exibido da situacao
    SituacaoLocacao(String rotulo) {
        this.rotulo = rotulo;
    }

    // TODO: devolve o rotulo da situacao
    @Override
    public String toString() {
        return rotulo;
    }
}
