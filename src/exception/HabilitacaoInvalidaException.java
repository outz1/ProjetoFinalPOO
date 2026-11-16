package exception;

/** RN02 e RN03 idade minima validade e categoria da CNH */
public class HabilitacaoInvalidaException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    // TODO: cria o erro de CNH ou idade invalida!
    public HabilitacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
