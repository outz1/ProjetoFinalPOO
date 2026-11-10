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

/** Uma locadora nova, gravando numa pasta temporária, mais fábricas de objetos de teste com dados únicos. */
final class Cenario {
    static final LocalDate RETIRADA = LocalDate.of(2026, 11, 5);
    static final LocalDate VALIDADE_CNH = LocalDate.of(2035, 1, 1);
    private static final List<Path> PASTAS = new ArrayList<>();
    private static int sequencia = 0;

    final Path pasta;
    final Locadora locadora;
    final Funcionario atendente;
    final Funcionario gerente;

    Cenario() throws Exception {
        pasta = Files.createTempDirectory("locadora-teste");
        PASTAS.add(pasta);
        locadora = new Locadora(new ArquivoDados(arquivo().toString()));
        atendente = funcionario(Cargo.ATENDENTE);
        gerente = funcionario(Cargo.GERENTE);
        locadora.incluirFuncionario(atendente);
        locadora.incluirFuncionario(gerente);
    }

    static Locadora locadoraVazia() throws IOException {
        Path pasta = Files.createTempDirectory("locadora-teste");
        PASTAS.add(pasta);
        return new Locadora(new ArquivoDados(pasta.resolve("locadora.dat").toString()));
    }

    Path arquivo() {
        return pasta.resolve("locadora.dat");
    }

    static LocalDate dia(int diasDepoisDaRetirada) {
        return RETIRADA.plusDays(diasDepoisDaRetirada);
    }

    // ---- fábricas (não cadastram nada)

    static String cpf() {
        return String.format("%011d", ++sequencia);
    }

    static String placa() {
        return String.format("TST%04d", ++sequencia);
    }

    static Carro carro(double base, boolean automatico) {
        return new Carro(placa(), "Fiat", "Argo", 2022, "Branco", 1000, base, 4, automatico, true);
    }

    static Moto moto(double base, int cilindradas) {
        return new Moto(placa(), "Honda", "CG", 2022, "Preta", 500, base, cilindradas);
    }

    static Utilitario utilitario(double base, double cargaKg) {
        return new Utilitario(placa(), "Fiat", "Fiorino", 2021, "Branco", 2000, base, cargaKg, "Furgão");
    }

    static Cliente cliente(String categoriaCnh, LocalDate nascimento, LocalDate validadeCnh) {
        return new Cliente("Cliente " + (++sequencia), cpf(), "", "", nascimento, "0123456789", categoriaCnh,
                validadeCnh, "");
    }

    static Cliente clienteHabilitado() {
        return cliente("AB", LocalDate.of(1990, 1, 1), VALIDADE_CNH);
    }

    static Funcionario funcionario(Cargo cargo) {
        int n = ++sequencia;
        return new Funcionario("Funcionário " + n, cpf(), "", "", LocalDate.of(1990, 1, 1), "M" + n, cargo);
    }

    // ---- cadastro na locadora do cenário

    Veiculo cadastrar(Veiculo veiculo) throws LocadoraException {
        locadora.incluirVeiculo(veiculo);
        return veiculo;
    }

    Cliente cadastrar(Cliente cliente) throws LocadoraException {
        locadora.incluirCliente(cliente);
        return cliente;
    }

    Locacao locar(Cliente cliente, Veiculo veiculo, LocalDate prevista, boolean seguro) throws LocadoraException {
        return locadora.realizarLocacao(cliente.getId(), veiculo.getId(), atendente.getId(), RETIRADA, prevista, seguro);
    }

    /** Apaga as pastas temporárias criadas pelos testes. */
    static void limpar() {
        for (Path pasta : PASTAS) {
            try (Stream<Path> arquivos = Files.walk(pasta)) {
                arquivos.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            } catch (IOException e) {
                // pasta temporária: se não der para apagar, o sistema limpa depois
            }
        }
    }
}
