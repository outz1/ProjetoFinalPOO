package exception;

/** RN10: placa e CPF não podem se repetir (vale também para matrícula e código). */
public class DadoDuplicadoException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    public DadoDuplicadoException(String campo, String valor) {
        super("Já existe um registro com " + campo + " " + valor + ".");
    }
}
