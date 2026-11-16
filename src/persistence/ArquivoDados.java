package persistence;

import model.Cliente;
import model.Funcionario;
import model.Locacao;
import model.Pessoa;
import model.Veiculo;
import service.Locadora;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Grava e le as quatro listas da locadora por serializacao todas no mesmo fluxo Assim uma locacao
 * carregada aponta para o mesmo objeto Veiculo/Cliente/Funcionario que esta nas listas
 */
public class ArquivoDados {
    private static final String CAMINHO = "dados/locadora.dat";

    private final Path caminho;

    // TODO: define o arquivo de dados padrao ou o informado
    public ArquivoDados() {
        this(CAMINHO);
    }

    // TODO: define o arquivo de dados padrao ou o informado
    public ArquivoDados(String caminho) {
        this.caminho = Path.of(caminho);
    }

    // TODO: devolve caminho
    public Path getCaminho() {
        return caminho;
    }

    // TODO: grava as quatro listas da locadora no arquivo
    public void salvar(Locadora locadora) throws IOException {
        Path pasta = caminho.toAbsolutePath().getParent();
        Files.createDirectories(pasta);
        try (ObjectOutputStream saida = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(caminho)))) {
            saida.writeObject(new ArrayList<>(locadora.getFuncionarios().listar()));
            saida.writeObject(new ArrayList<>(locadora.getClientes().listar()));
            saida.writeObject(new ArrayList<>(locadora.getVeiculos().listar()));
            saida.writeObject(new ArrayList<>(locadora.getLocacoes().listar()));
        }
    }

    // TODO: le as quatro listas do arquivo se ele existir e reajusta os contadores
    @SuppressWarnings("unchecked")
    public void carregar(Locadora locadora) throws IOException, ClassNotFoundException {
        if (!Files.exists(caminho))
            return;
        ArrayList<Funcionario> funcionarios;
        ArrayList<Cliente> clientes;
        ArrayList<Veiculo> veiculos;
        ArrayList<Locacao> locacoes;
        try (ObjectInputStream entrada = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(caminho)))) {
            funcionarios = (ArrayList<Funcionario>) entrada.readObject();
            clientes = (ArrayList<Cliente>) entrada.readObject();
            veiculos = (ArrayList<Veiculo>) entrada.readObject();
            locacoes = (ArrayList<Locacao>) entrada.readObject();
        }
        locadora.getFuncionarios().setItens(funcionarios);
        locadora.getClientes().setItens(clientes);
        locadora.getVeiculos().setItens(veiculos);
        locadora.getLocacoes().setItens(locacoes);

        // os contadores sao estaticos e nao vao para o arquivo reajusta para depois do maior codigo lido
        int maiorPessoa = Math.max(
                funcionarios.stream().mapToInt(Funcionario::getId).max().orElse(0),
                clientes.stream().mapToInt(Cliente::getId).max().orElse(0));
        Pessoa.atualizarContador(maiorPessoa);
        Veiculo.atualizarContador(veiculos.stream().mapToInt(Veiculo::getId).max().orElse(0));
        Locacao.atualizarContador(locacoes.stream().mapToInt(Locacao::getId).max().orElse(0));
    }
}
