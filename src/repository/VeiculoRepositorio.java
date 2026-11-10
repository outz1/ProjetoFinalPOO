package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.SituacaoVeiculo;
import model.Veiculo;
import java.util.List;

public class VeiculoRepositorio extends RepositorioBase<Veiculo> {
    public VeiculoRepositorio() {
        super("veículo");
    }

    public Veiculo buscarPorPlaca(String placa) throws RegistroNaoEncontradoException {
        for (Veiculo v : itens) {
            if (v.getPlaca().equalsIgnoreCase(placa.trim()))
                return v;
        }
        throw new RegistroNaoEncontradoException("veículo", "placa " + placa);
    }

    public List<Veiculo> listarPorSituacao(SituacaoVeiculo situacao) {
        return itens.stream().filter(v -> v.getSituacao() == situacao).toList();
    }

    public List<Veiculo> listarPorCategoria(String categoria) {
        return itens.stream().filter(v -> v.getCategoria().equalsIgnoreCase(categoria.trim())).toList();
    }

    /** RN10: a placa não pode se repetir. */
    @Override
    protected void validarInclusao(Veiculo veiculo) throws DadoDuplicadoException {
        for (Veiculo outro : itens) {
            if (outro.getId() != veiculo.getId() && outro.getPlaca().equalsIgnoreCase(veiculo.getPlaca()))
                throw new DadoDuplicadoException("placa", veiculo.getPlaca());
        }
    }
}
