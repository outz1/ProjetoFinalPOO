package exception;

/** RN10: placa e CPF nao podem se repetir vale tambem para matricula e codigo */
public class DadoDuplicadoException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    // TODO: cria o erro de valor repetido placa CPF matricula!
    public DadoDuplicadoException(String campo, String valor) {
        super("Já existe um registro com " + campo + " " + valor + ".");
    }
}
