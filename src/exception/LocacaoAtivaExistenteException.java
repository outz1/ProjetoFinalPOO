package exception;

/** RN04: cada cliente pode ter só uma locação ativa por vez. */
public class LocacaoAtivaExistenteException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    public LocacaoAtivaExistenteException(String nomeCliente) {
        super("O cliente " + nomeCliente + " já possui uma locação ativa.");
    }
}
