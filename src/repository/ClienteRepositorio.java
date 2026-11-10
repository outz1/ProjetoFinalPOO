package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.Cliente;

public class ClienteRepositorio extends RepositorioBase<Cliente> {
    public ClienteRepositorio() {
        super("cliente");
    }

    public Cliente buscarPorCpf(String cpf) throws RegistroNaoEncontradoException {
        for (Cliente c : itens) {
            if (c.getCpf().equals(cpf.trim()))
                return c;
        }
        throw new RegistroNaoEncontradoException("cliente", "CPF " + cpf);
    }

    /** RN10: o CPF não pode se repetir. */
    @Override
    protected void validarInclusao(Cliente cliente) throws DadoDuplicadoException {
        for (Cliente outro : itens) {
            if (outro.getId() != cliente.getId() && outro.getCpf().equals(cliente.getCpf()))
                throw new DadoDuplicadoException("CPF", cliente.getCpf());
        }
    }
}
