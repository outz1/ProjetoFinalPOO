package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.SituacaoVeiculo;
import model.Veiculo;
import java.util.List;

public class VeiculoRepositorio extends RepositorioBase<Veiculo> {
    // TODO: cria o repositorio de veiculos
    public VeiculoRepositorio() {
        super("veículo");
    }

    // TODO: busca o veiculo pela placa
    public Veiculo buscarPorPlaca(String placa) throws RegistroNaoEncontradoException {
        for (Veiculo v : itens) {
            if (v.getPlaca().equalsIgnoreCase(placa.trim()))
                return v;
        }
        throw new RegistroNaoEncontradoException("veículo", "placa " + placa);
    }

    // TODO: lista os veiculos na situacao informada
    public List<Veiculo> listarPorSituacao(SituacaoVeiculo situacao) {
        return itens.stream().filter(v -> v.getSituacao() == situacao).toList();
    }

    // TODO: lista os veiculos da categoria informada
    public List<Veiculo> listarPorCategoria(String categoria) {
        return itens.stream().filter(v -> v.getCategoria().equalsIgnoreCase(categoria.trim())).toList();
    }

    // TODO: recusa placa repetida RN10
    @Override
    protected void validarInclusao(Veiculo veiculo) throws DadoDuplicadoException {
        for (Veiculo outro : itens) {
            if (outro.getId() != veiculo.getId() && outro.getPlaca().equalsIgnoreCase(veiculo.getPlaca()))
                throw new DadoDuplicadoException("placa", veiculo.getPlaca());
        }
    }
}
