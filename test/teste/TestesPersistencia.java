package teste;

import exception.LocadoraException;
import model.Carro;
import model.Cliente;
import model.Locacao;
import model.Moto;
import model.SituacaoLocacao;
import model.SituacaoVeiculo;
import model.Utilitario;
import model.Veiculo;
import persistence.ArquivoDados;
import service.Locadora;
import java.nio.file.Files;
import java.nio.file.Path;
import static teste.Cenario.dia;
import static teste.Verificador.caso;
import static teste.Verificador.grupo;
import static teste.Verificador.igual;
import static teste.Verificador.lanca;
import static teste.Verificador.verdadeiro;

/** Gravação e leitura de objetos em arquivo (item g) e carga das listas (item m). */
final class TestesPersistencia {
    private TestesPersistencia() {
    }

    private static Locadora recarregar(Cenario c) throws LocadoraException {
        Locadora nova = new Locadora(new ArquivoDados(c.arquivo().toString()));
        nova.carregarDados();
        return nova;
    }

    static void executar() {
        grupo("Persistência: arquivo dados/locadora.dat por serialização");
        caso("CT65", "RF13, item g", "Salvar e carregar preserva todos os dados, inclusive o tipo de cada veículo", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Carro carro = (Carro) c.cadastrar(Cenario.carro(150, true));
            Moto moto = (Moto) c.cadastrar(Cenario.moto(80, 650));
            Utilitario utilitario = (Utilitario) c.cadastrar(Cenario.utilitario(100, 1800));
            Locacao l = c.locar(cliente, carro, dia(10), true);

            Locadora lida = recarregar(c);
            igual(2, lida.getFuncionarios().listar().size());
            igual(1, lida.getClientes().listar().size());
            igual(3, lida.getVeiculos().listar().size());
            igual(1, lida.getLocacoes().listar().size());

            Veiculo carroLido = lida.getVeiculos().buscarPorId(carro.getId());
            verdadeiro(carroLido instanceof Carro, "o carro volta como Carro");
            verdadeiro(lida.getVeiculos().buscarPorId(moto.getId()) instanceof Moto, "a moto volta como Moto");
            verdadeiro(lida.getVeiculos().buscarPorId(utilitario.getId()) instanceof Utilitario, "o utilitário volta como Utilitario");
            igual(carro.getPlaca(), carroLido.getPlaca());
            igual(165.00, carroLido.calcularValorDiaria());
            igual(SituacaoVeiculo.LOCADO, carroLido.getSituacao());

            Locacao lidaLocacao = lida.getLocacoes().buscarPorId(l.getId());
            igual(SituacaoLocacao.ATIVA, lidaLocacao.getSituacao());
            igual(1817.50, lidaLocacao.getValorPrevisto());
            igual(cliente.getCpf(), lidaLocacao.getCliente().getCpf());
        });
        caso("CT66", "RF13", "Depois de carregar, as locações apontam para os mesmos objetos das listas (sem cópias soltas)", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(3), false);

            Locadora lida = recarregar(c);
            Locacao lidaLocacao = lida.getLocacoes().buscarPorId(l.getId());
            verdadeiro(lidaLocacao.getVeiculo() == lida.getVeiculos().buscarPorId(carro.getId()), "mesmo veículo");
            verdadeiro(lidaLocacao.getCliente() == lida.getClientes().buscarPorId(cliente.getId()), "mesmo cliente");
            verdadeiro(lidaLocacao.getFuncionario() == lida.getFuncionarios().buscarPorId(c.atendente.getId()), "mesmo funcionário");

            // a devolução na locadora recarregada libera o veículo que está na lista
            lida.registrarDevolucao(l.getId(), dia(3), 1500);
            igual(SituacaoVeiculo.DISPONIVEL, lida.getVeiculos().buscarPorId(carro.getId()).getSituacao());
        });
        caso("CT67", "RF13", "Cada alteração é salva sozinha: uma locadora nova já enxerga o que foi feito, sem chamar salvarDados()", () -> {
            Cenario c = new Cenario();
            igual(2, recarregar(c).getFuncionarios().listar().size());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            igual(1, recarregar(c).getVeiculos().listar().size());
            carro.setCor("Verde");
            c.locadora.alterarVeiculo(carro);
            igual("Verde", recarregar(c).getVeiculos().buscarPorId(carro.getId()).getCor());
            c.locadora.enviarParaManutencao(carro.getId());
            igual(SituacaoVeiculo.MANUTENCAO, recarregar(c).getVeiculos().buscarPorId(carro.getId()).getSituacao());
            c.locadora.excluirVeiculo(carro.getId());
            igual(0, recarregar(c).getVeiculos().listar().size());
        });
        caso("CT68", "RF13", "Sem arquivo ainda, carregar deixa a locadora vazia e sem erro", () -> {
            Locadora vazia = Cenario.locadoraVazia();
            vazia.carregarDados();
            igual(0, vazia.getVeiculos().listar().size());
            igual(0, vazia.getLocacoes().listar().size());
        });
        caso("CT69", "RF13, item l", "Arquivo corrompido gera LocadoraException com mensagem e não apaga o arquivo", () -> {
            Cenario c = new Cenario();
            Files.writeString(c.arquivo(), "isto não é um arquivo serializado");
            Locadora lida = new Locadora(new ArquivoDados(c.arquivo().toString()));
            LocadoraException erro = lanca(LocadoraException.class, lida::carregarDados);
            verdadeiro(erro.getMessage().contains("Não foi possível ler"), "mensagem clara: " + erro.getMessage());
            verdadeiro(Files.exists(c.arquivo()), "o arquivo original continua lá");
            igual(0, lida.getVeiculos().listar().size());
        });
        caso("CT70", "RF12, RF13", "Em outra execução do programa, os códigos continuam depois do maior código gravado", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(3), false);
            int maiorPessoa = Math.max(cliente.getId(), Math.max(c.atendente.getId(), c.gerente.getId()));

            // os contadores são estáticos: só uma JVM nova prova que o reajuste depois da leitura funciona
            String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
            Process processo = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
                    "teste.ContadoresEmNovaJvm", c.arquivo().toString()).redirectErrorStream(true).start();
            String saida = new String(processo.getInputStream().readAllBytes());
            igual(0, processo.waitFor());
            String[] linhas = saida.trim().split("\\R");
            String[] ids = linhas[linhas.length - 1].split(" ");
            igual(carro.getId() + 1, Integer.parseInt(ids[0]));
            igual(maiorPessoa + 1, Integer.parseInt(ids[1]));
            igual(l.getId() + 1, Integer.parseInt(ids[2]));
        });
        caso("CT71", "item m", "As listas lidas do arquivo são ArrayList e o repositório as manipula", () -> {
            Cenario c = new Cenario();
            c.cadastrar(Cenario.carro(100, false));
            Locadora lida = recarregar(c);
            verdadeiro(lida.getVeiculos().getItens() instanceof java.util.ArrayList, "ArrayList");
            igual(1, lida.getVeiculos().getItens().size());
        });
    }
}
