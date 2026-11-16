package view;

import exception.LocadoraException;
import model.Cargo;
import model.Funcionario;
import service.Locadora;
import java.time.LocalDate;

/** UC03 gerenciar funcionarios */
public class TelaFuncionarios extends Tela {
    private static final String INCLUIR = "Incluir funcionário";
    private static final String ALTERAR = "Alterar funcionário";
    private static final String EXCLUIR = "Excluir funcionário";
    private static final String CONSULTAR = "Consultar por matrícula";
    private static final String LISTAR = "Listar todos";

    // TODO: cria a tela de funcionarios
    public TelaFuncionarios(Locadora locadora) {
        super(locadora);
    }

    // TODO: devolve o titulo da tela
    @Override
    protected String titulo() {
        return "Funcionários";
    }

    // TODO: devolve as opcoes do menu
    @Override
    protected String[] opcoes() {
        return new String[]{INCLUIR, ALTERAR, EXCLUIR, CONSULTAR, LISTAR};
    }

    // TODO: executa a opcao escolhida no menu
    @Override
    protected void executar(String opcao) throws LocadoraException {
        switch (opcao) {
            case INCLUIR -> incluir();
            case ALTERAR -> alterar();
            case EXCLUIR -> excluir();
            case CONSULTAR -> mostrarDetalhe("Funcionário", buscar("Matrícula do funcionário:"));
            case LISTAR -> mostrarLista("Todos os funcionários", locadora.getFuncionarios().listar());
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }

    // TODO: pede a matricula e busca o funcionario
    private Funcionario buscar(String mensagem) throws LocadoraException {
        return locadora.getFuncionarios().buscarPorMatricula(lerTexto(mensagem));
    }

    // TODO: pede o cargo do funcionario
    private Cargo lerCargo() {
        Cargo[] cargos = Cargo.values();
        String[] rotulos = {cargos[0].toString(), cargos[1].toString()};
        return cargos[lerOpcao("Cargo:", rotulos)];
    }

    // TODO: le os dados e inclui o funcionario
    private void incluir() throws LocadoraException {
        String nome = lerTexto("Nome:");
        String cpf = lerTexto("CPF:");
        String telefone = lerTexto("Telefone:");
        String email = lerTexto("E-mail:");
        LocalDate nascimento = lerData("Data de nascimento");
        String matricula = lerTexto("Matrícula:");
        Cargo cargo = lerCargo();

        Funcionario funcionario = new Funcionario(nome, cpf, telefone, email, nascimento, matricula, cargo);
        locadora.incluirFuncionario(funcionario);
        mostrar("Funcionário incluído com o código " + funcionario.getId() + ".");
    }

    // TODO: mostra os dados atuais e altera o funcionario
    private void alterar() throws LocadoraException {
        Funcionario atual = buscar("Matrícula do funcionário a alterar:");
        String nome = lerTexto("Nome:", atual.getNome());
        String cpf = lerTexto("CPF:", atual.getCpf());
        String telefone = lerTexto("Telefone:", atual.getTelefone());
        String email = lerTexto("E-mail:", atual.getEmail());
        LocalDate nascimento = lerData("Data de nascimento", atual.getDataNascimento());
        String matricula = lerTexto("Matrícula:", atual.getMatricula());
        Cargo cargo = lerCargo();

        atual.setNome(nome);
        atual.setCpf(cpf);
        atual.setTelefone(telefone);
        atual.setEmail(email);
        atual.setDataNascimento(nascimento);
        atual.setMatricula(matricula);
        atual.setCargo(cargo);
        locadora.alterarFuncionario(atual);
        mostrar("Funcionário alterado.");
    }

    // TODO: confirma e exclui o funcionario
    private void excluir() throws LocadoraException {
        Funcionario f = buscar("Matrícula do funcionário a excluir:");
        if (lerSimNao("Excluir o funcionário " + f.getNome() + "?")) {
            locadora.excluirFuncionario(f.getId());
            mostrar("Funcionário excluído.");
        }
    }
}
