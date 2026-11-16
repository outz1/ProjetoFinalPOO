package exception;

/** RN04 cada cliente pode ter so uma locacao ativa por vez */
public class LocacaoAtivaExistenteException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    // TODO: cria o erro de cliente que ja tem locacao ativa!
    public LocacaoAtivaExistenteException(String nomeCliente) {
        super("O cliente " + nomeCliente + " já possui uma locação ativa.");
    }
}
