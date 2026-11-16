package view;

/** O usuario fechou ou cancelou uma caixa de dialogo abandona a operacao e volta ao menu Nao e erro */
public class OperacaoCanceladaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // TODO: cria o sinal de operacao canceladaa pelo usuario
    public OperacaoCanceladaException() {
        super("Operação cancelada pelo usuário.");
    }
}
