package exception;

/** RN02 e RN03: idade mínima, validade e categoria da CNH. */
public class HabilitacaoInvalidaException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    public HabilitacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
