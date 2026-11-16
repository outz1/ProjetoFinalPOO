package teste;

import exception.LocadoraException;
import model.Carro;
import model.Cargo;
import model.Cliente;
import model.Funcionario;
import model.Locacao;
import model.Moto;
import model.Utilitario;
import model.Veiculo;
import persistence.ArquivoDados;
import service.Locadora;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Uma locadora nova gravando numa pasta temporaria mais fabricas de objetos de teste com dados unicos */
final class Cenario {
    static final LocalDate RETIRADA = LocalDate.of(2026, 11, 5);
    static final LocalDate VALIDADE_CNH = LocalDate.of(2035, 1, 1);
    private static final List<Path> PASTAS = new ArrayList<>();
    private static int sequencia = 0;

    final Path pasta;
    final Locadora locadora;
    final Funcionario atendente;
    final Funcionario gerente;

    // TODO: cria uma locadora nova em pasta temporaria com atendente e gerente
    Cenario() throws Exception {
        pasta = Files.createTempDirectory("locadora-teste");
        PASTAS.add(pasta);
        locadora = new Locadora(new ArquivoDados(arquivo().toString()));
        atendente = funcionario(Cargo.ATENDENTE);
        gerente = funcionario(Cargo.GERENTE);
        locadora.incluirFuncionario(atendente);
        locadora.incluirFuncionario(gerente);
    }

    // TODO: cria uma locadora vazia em pasta temporaria
    static Locadora locadoraVazia() throws IOException {
        Path pasta = Files.createTempDirectory("locadora-teste");
        PASTAS.add(pasta);
        return new Locadora(new ArquivoDados(pasta.resolve("locadora.dat").toString()));
    }

    // TODO: devolve o caminho do arquivo de dados do cenario
    Path arquivo() {
        return pasta.resolve("locadora.dat");
    }

    // TODO: devolve a data N dias depois da retirada
    static LocalDate dia(int diasDepoisDaRetirada) {
        return RETIRADA.plusDays(diasDepoisDaRetirada);
    }

    // TODO: gera um CPF unico
    static String cpf() {
        return String.format("%011d", ++sequencia);
    }

    // TODO: gera uma placa unica
    static String placa() {
        return String.format("TST%04d", ++sequencia);
    }

    // TODO: cria um carro de teste sem cadastrar
    static Carro carro(double base, boolean automatico) {
        return new Carro(placa(), "Fiat", "Argo", 2022, "Branco", 1000, base, 4, automatico, true);
    }

    // TODO: cria uma moto de teste sem cadastrar
    static Moto moto(double base, int cilindradas) {
        return new Moto(placa(), "Honda", "CG", 2022, "Preta", 500, base, cilindradas);
    }

    // TODO: cria um utilitario de teste sem cadastrar
    static Utilitario utilitario(double base, double cargaKg) {
        return new Utilitario(placa(), "Fiat", "Fiorino", 2021, "Branco", 2000, base, cargaKg, "Furgão");
    }

    // TODO: cria um cliente de teste com a CNH e as datas informadas
    static Cliente cliente(String categoriaCnh, LocalDate nascimento, LocalDate validadeCnh) {
        return new Cliente("Cliente " + (++sequencia), cpf(), "", "", nascimento, "0123456789", categoriaCnh,
                validadeCnh, "");
    }

    // TODO: cria um cliente de teste com CNH AB valida
    static Cliente clienteHabilitado() {
        return cliente("AB", LocalDate.of(1990, 1, 1), VALIDADE_CNH);
    }

    // TODO: cria um funcionario de teste com o cargo informado
    static Funcionario funcionario(Cargo cargo) {
        int n = ++sequencia;
        return new Funcionario("Funcionário " + n, cpf(), "", "", LocalDate.of(1990, 1, 1), "M" + n, cargo);
    }

    // TODO: cadastra o veiculo ou cliente na locadora do cenario
    Veiculo cadastrar(Veiculo veiculo) throws LocadoraException {
        locadora.incluirVeiculo(veiculo);
        return veiculo;
    }

    // TODO: cadastra o veiculo ou cliente na locadora do cenario
    Cliente cadastrar(Cliente cliente) throws LocadoraException {
        locadora.incluirCliente(cliente);
        return cliente;
    }

    // TODO: registra uma locacao com o atendente do cenario
    Locacao locar(Cliente cliente, Veiculo veiculo, LocalDate prevista, boolean seguro) throws LocadoraException {
        return locadora.realizarLocacao(cliente.getId(), veiculo.getId(), atendente.getId(), RETIRADA, prevista, seguro);
    }

    // TODO: apaga as pastas temporarias criadas pelos testes
    static void limpar() {
        for (Path pasta : PASTAS) {
            try (Stream<Path> arquivos = Files.walk(pasta)) {
                arquivos.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            } catch (IOException e) {
                // pasta temporaria se nao der para apagar o sistema limpa depois
            }
        }
    }
}
