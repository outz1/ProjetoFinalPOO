package view;

import exception.LocadoraException;
import model.Cliente;
import service.Locadora;
import java.time.LocalDate;

/** UC02: gerenciar clientes. */
public class TelaClientes extends Tela {
    private static final String INCLUIR = "Incluir cliente";
    private static final String ALTERAR = "Alterar cliente";
    private static final String EXCLUIR = "Excluir cliente";
    private static final String CONSULTAR = "Consultar por CPF";
    private static final String LISTAR = "Listar todos";

    private static final String[] CATEGORIAS_CNH = {"A", "B", "C", "D", "E", "AB", "AC", "AD", "AE"};

    public TelaClientes(Locadora locadora) {
        super(locadora);
    }

    @Override
    protected String titulo() {
        return "Clientes";
    }

    @Override
    protected String[] opcoes() {
        return new String[]{INCLUIR, ALTERAR, EXCLUIR, CONSULTAR, LISTAR};
    }

    @Override
    protected void executar(String opcao) throws LocadoraException {
        switch (opcao) {
            case INCLUIR -> incluir();
            case ALTERAR -> alterar();
            case EXCLUIR -> excluir();
            case CONSULTAR -> mostrarDetalhe("Cliente", buscar("CPF do cliente:"));
            case LISTAR -> mostrarLista("Todos os clientes", locadora.getClientes().listar());
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }

    private Cliente buscar(String mensagem) throws LocadoraException {
        return locadora.getClientes().buscarPorCpf(lerTexto(mensagem));
    }

    private void incluir() throws LocadoraException {
        String nome = lerTexto("Nome:");
        String cpf = lerTexto("CPF:");
        String telefone = lerTexto("Telefone:");
        String email = lerTexto("E-mail:");
        LocalDate nascimento = lerData("Data de nascimento");
        String numeroCnh = lerTexto("Número da CNH:");
        String categoria = CATEGORIAS_CNH[lerOpcao("Categoria da CNH:", CATEGORIAS_CNH)];
        LocalDate validade = lerData("Validade da CNH");
        String endereco = lerTexto("Endereço:");

        Cliente cliente = new Cliente(nome, cpf, telefone, email, nascimento, numeroCnh, categoria, validade, endereco);
        locadora.incluirCliente(cliente);
        mostrar("Cliente incluído com o código " + cliente.getId() + ".");
    }

    private void alterar() throws LocadoraException {
        Cliente atual = buscar("CPF do cliente a alterar:");
        String nome = lerTexto("Nome:", atual.getNome());
        String cpf = lerTexto("CPF:", atual.getCpf());
        String telefone = lerTexto("Telefone:", atual.getTelefone());
        String email = lerTexto("E-mail:", atual.getEmail());
        LocalDate nascimento = lerData("Data de nascimento", atual.getDataNascimento());
        String numeroCnh = lerTexto("Número da CNH:", atual.getNumeroCnh());
        String categoria = lerTexto("Categoria da CNH (A, B, C, D, E ou AB, AC...):", atual.getCategoriaCnh());
        LocalDate validade = lerData("Validade da CNH", atual.getValidadeCnh());
        String endereco = lerTexto("Endereço:", atual.getEndereco());

        atual.setNome(nome);
        atual.setCpf(cpf);
        atual.setTelefone(telefone);
        atual.setEmail(email);
        atual.setDataNascimento(nascimento);
        atual.setNumeroCnh(numeroCnh);
        atual.setCategoriaCnh(categoria);
        atual.setValidadeCnh(validade);
        atual.setEndereco(endereco);
        locadora.alterarCliente(atual);
        mostrar("Cliente alterado.");
    }

    private void excluir() throws LocadoraException {
        Cliente c = buscar("CPF do cliente a excluir:");
        if (lerSimNao("Excluir o cliente " + c.getNome() + "?")) {
            locadora.excluirCliente(c.getId());
            mostrar("Cliente excluído.");
        }
    }
}
