package exception;

/** Base de todas as exceções de regra de negócio da locadora. */
public class LocadoraException extends Exception {
    private static final long serialVersionUID = 1L;

    public LocadoraException(String mensagem) {
        super(mensagem);
    }

    public LocadoraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
