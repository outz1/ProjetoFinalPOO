package model;

public enum TipoAlteracao {
    INCLUSAO("Inclusão"),
    ALTERACAO("Alteração"),
    EXCLUSAO("Exclusão");

    private final String rotulo;

    // TODO: guarda o rotulo exibido do tipo
    TipoAlteracao(String rotulo) {
        this.rotulo = rotulo;
    }

    // TODO: devolve o rotulo do tipo
    @Override
    public String toString() {
        return rotulo;
    }
}
