package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.Funcionario;

public class FuncionarioRepositorio extends RepositorioBase<Funcionario> {
    public FuncionarioRepositorio() {
        super("funcionário");
    }

    public Funcionario buscarPorMatricula(String matricula) throws RegistroNaoEncontradoException {
        for (Funcionario f : itens) {
            if (f.getMatricula().equalsIgnoreCase(matricula.trim()))
                return f;
        }
        throw new RegistroNaoEncontradoException("funcionário", "matrícula " + matricula);
    }

    /** RN10: o CPF e a matrícula não podem se repetir. */
    @Override
    protected void validarInclusao(Funcionario funcionario) throws DadoDuplicadoException {
        for (Funcionario outro : itens) {
            if (outro.getId() == funcionario.getId())
                continue;
            if (outro.getCpf().equals(funcionario.getCpf()))
                throw new DadoDuplicadoException("CPF", funcionario.getCpf());
            if (outro.getMatricula().equalsIgnoreCase(funcionario.getMatricula()))
                throw new DadoDuplicadoException("matrícula", funcionario.getMatricula());
        }
    }
}
