package exception;

/** RN01 so pode ser locado o veiculo na situacao Disponivel */
public class VeiculoIndisponivelException extends LocadoraException {
    private static final long serialVersionUID = 1L;

    // TODO: cria o erro de veiculo que nao esta disponivel RN01
    public VeiculoIndisponivelException(String placa, String situacao) {
        super("O veículo " + placa + " não está disponível (situação: " + situacao + ").");
    }
}
