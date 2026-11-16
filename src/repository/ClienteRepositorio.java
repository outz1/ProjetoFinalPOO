package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.Cliente;

public class ClienteRepositorio extends RepositorioBase<Cliente> {
    // TODO: cria o repositorio de clientes
    public ClienteRepositorio() {
        super("cliente");
    }

    // TODO: busca o cliente pelo CPF
    public Cliente buscarPorCpf(String cpf) throws RegistroNaoEncontradoException {
        for (Cliente c : itens) {
            if (c.getCpf().equals(cpf.trim()))
                return c;
        }
        throw new RegistroNaoEncontradoException("cliente", "CPF " + cpf);
    }

    // TODO: recusa CPF repetido RN10
    @Override
    protected void validarInclusao(Cliente cliente) throws DadoDuplicadoException {
        for (Cliente outro : itens) {
            if (outro.getId() != cliente.getId() && outro.getCpf().equals(cliente.getCpf()))
                throw new DadoDuplicadoException("CPF", cliente.getCpf());
        }
    }
}
