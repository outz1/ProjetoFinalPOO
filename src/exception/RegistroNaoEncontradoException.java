package exception;

/** Busca sem resultado. */
public class RegistroNaoEncontradoException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    public RegistroNaoEncontradoException(String entidade, String criterio) {
        super("Não foi encontrado nenhum registro de " + entidade + " com " + criterio + ".");
    }
}
