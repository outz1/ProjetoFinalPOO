package exception;

/** Base de todas as excecoes de regra de negocio da locadora */
public class LocadoraException extends Exception {
    private static final long serialVersionUID = 1L;

    // TODO: cria a excecao de regra de negocio com ou sem causa
    public LocadoraException(String mensagem) {
        super(mensagem);
    }

    // TODO: cria a excecao de regra de negocio com ou sem causa!
    public LocadoraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
