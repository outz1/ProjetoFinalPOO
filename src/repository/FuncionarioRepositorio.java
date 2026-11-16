package repository;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.Funcionario;

public class FuncionarioRepositorio extends RepositorioBase<Funcionario> {
    // TODO: cria o repositorio de funcionarios
    public FuncionarioRepositorio() {
        super("funcionário");
    }

    // TODO: busca o funcionario pela matricula
    public Funcionario buscarPorMatricula(String matricula) throws RegistroNaoEncontradoException {
        for (Funcionario f : itens) {
            if (f.getMatricula().equalsIgnoreCase(matricula.trim()))
                return f;
        }
        throw new RegistroNaoEncontradoException("funcionário", "matrícula " + matricula);
    }

    // TODO: recusa CPF ou matricula repetidos RN10
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
