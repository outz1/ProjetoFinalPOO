package exception;

/** RN01: só pode ser locado o veículo na situação Disponível. */
public class VeiculoIndisponivelException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    public VeiculoIndisponivelException(String placa, String situacao) {
        super("O veículo " + placa + " não está disponível (situação: " + situacao + ").");
    }
}
