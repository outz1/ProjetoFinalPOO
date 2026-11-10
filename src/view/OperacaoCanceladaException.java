package view;

/** O usuário fechou ou cancelou uma caixa de diálogo: abandona a operação e volta ao menu. Não é erro. */
public class OperacaoCanceladaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OperacaoCanceladaException() {
        super("Operação cancelada pelo usuário.");
    }
}
