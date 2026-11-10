package teste;

import exception.DadoDuplicadoException;
import exception.RegistroNaoEncontradoException;
import model.Cargo;
import model.Carro;
import model.Cliente;
import model.Funcionario;
import model.Locacao;
import model.Moto;
import model.SituacaoVeiculo;
import model.Utilitario;
import model.Veiculo;
import java.time.LocalDate;
import java.util.List;
import static teste.Cenario.dia;
import static teste.Verificador.caso;
import static teste.Verificador.grupo;
import static teste.Verificador.igual;
import static teste.Verificador.lanca;
import static teste.Verificador.verdadeiro;

/** Inclusão, exclusão, alteração, consulta e lista (item i) de veículos, clientes e funcionários. */
final class TestesCrud {
    private TestesCrud() {
    }

    static void executar() {
        grupo("CRUD: veículos das três categorias");
        caso("CT56", "RF01, item i", "Incluir, consultar (código e placa) e listar veículos de cada categoria", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Veiculo moto = c.cadastrar(Cenario.moto(80, 160));
            Veiculo utilitario = c.cadastrar(Cenario.utilitario(120, 800));
            igual(3, c.locadora.getVeiculos().listar().size());
            igual(moto, c.locadora.getVeiculos().buscarPorId(moto.getId()));
            igual(utilitario, c.locadora.getVeiculos().buscarPorPlaca(utilitario.getPlaca().toLowerCase()));
            igual(List.of(carro), c.locadora.getVeiculos().listarPorCategoria("Carro"));
            igual(List.of(moto), c.locadora.getVeiculos().listarPorCategoria("Moto"));
            igual(List.of(utilitario), c.locadora.getVeiculos().listarPorCategoria("Utilitário"));
            igual(3, c.locadora.getVeiculos().listarPorSituacao(SituacaoVeiculo.DISPONIVEL).size());
        });
        caso("CT57", "RF01, item i", "Alterar campos comuns e específicos de cada categoria", () -> {
            Cenario c = new Cenario();
            Carro carro = (Carro) c.cadastrar(Cenario.carro(100, false));
            Moto moto = (Moto) c.cadastrar(Cenario.moto(80, 160));
            Utilitario utilitario = (Utilitario) c.cadastrar(Cenario.utilitario(120, 800));

            carro.setCor("Vermelho");
            carro.setValorDiariaBase(200);
            carro.setAutomatico(true);
            c.locadora.alterarVeiculo(carro);
            igual("Vermelho", carro.getCor());
            igual(220.00, carro.calcularValorDiaria());

            moto.setCilindradas(650);
            c.locadora.alterarVeiculo(moto);
            igual(96.00, moto.calcularValorDiaria());

            utilitario.setCapacidadeCargaKg(2000);
            c.locadora.alterarVeiculo(utilitario);
            igual("C", utilitario.getCnhExigida());
        });
        caso("CT58", "RN10, item i", "Alterar para a placa de outro veículo é recusado e o cadastro volta ao que estava gravado", () -> {
            Cenario c = new Cenario();
            Veiculo a = c.cadastrar(Cenario.carro(100, false));
            Veiculo b = c.cadastrar(Cenario.carro(100, false));
            String placaOriginal = b.getPlaca();
            b.setPlaca(a.getPlaca());
            lanca(DadoDuplicadoException.class, () -> c.locadora.alterarVeiculo(b));
            Veiculo depois = c.locadora.getVeiculos().buscarPorId(b.getId());
            igual(placaOriginal, depois.getPlaca());
            // alterar mantendo a própria placa não conta como duplicidade
            depois.setCor("Azul");
            c.locadora.alterarVeiculo(depois);
            igual("Azul", c.locadora.getVeiculos().buscarPorId(b.getId()).getCor());
        });
        caso("CT59", "item i", "Alterar um veículo mantém as locações apontando para o mesmo objeto", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(3), false);
            carro.setPlaca("NEW1A23");
            c.locadora.alterarVeiculo(carro);
            igual("NEW1A23", l.getVeiculo().getPlaca());
            verdadeiro(l.getVeiculo() == c.locadora.getVeiculos().buscarPorId(carro.getId()), "mesma instância");
        });
        caso("CT60", "RF01, item i", "Excluir veículo remove da lista; consulta posterior não encontra", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            String placa = carro.getPlaca();
            c.locadora.excluirVeiculo(carro.getId());
            igual(0, c.locadora.getVeiculos().listar().size());
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getVeiculos().buscarPorPlaca(placa));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.excluirVeiculo(carro.getId()));
        });

        grupo("CRUD: clientes e funcionários");
        caso("CT61", "RF03, item i", "Cliente: incluir, consultar por CPF e código, listar, alterar e excluir", () -> {
            Cenario c = new Cenario();
            Cliente ana = c.cadastrar(Cenario.clienteHabilitado());
            Cliente beto = c.cadastrar(Cenario.clienteHabilitado());
            igual(2, c.locadora.getClientes().listar().size());
            igual(ana, c.locadora.getClientes().buscarPorCpf(ana.getCpf()));
            igual(beto, c.locadora.getClientes().buscarPorId(beto.getId()));

            ana.setNome("Ana Maria");
            ana.setCategoriaCnh("C");
            ana.setValidadeCnh(LocalDate.of(2040, 1, 1));
            c.locadora.alterarCliente(ana);
            igual("Ana Maria", ana.getNome());
            igual("C", ana.getCategoriaCnh());

            c.locadora.excluirCliente(ana.getId());
            igual(1, c.locadora.getClientes().listar().size());
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getClientes().buscarPorCpf(ana.getCpf()));
        });
        caso("CT62", "RN10, item i", "Cliente: alterar o CPF para o de outro é recusado e o cadastro volta ao que estava gravado", () -> {
            Cenario c = new Cenario();
            Cliente ana = c.cadastrar(Cenario.clienteHabilitado());
            Cliente beto = c.cadastrar(Cenario.clienteHabilitado());
            String cpfOriginal = beto.getCpf();
            beto.setNome("Novo nome");
            beto.setCpf(ana.getCpf());
            lanca(DadoDuplicadoException.class, () -> c.locadora.alterarCliente(beto));
            Cliente depois = c.locadora.getClientes().buscarPorId(beto.getId());
            igual(cpfOriginal, depois.getCpf());
            verdadeiro(!"Novo nome".equals(depois.getNome()), "o nome não pode ter ficado alterado");
        });
        caso("CT63", "RF04, item i", "Funcionário: incluir, consultar por matrícula, listar, alterar cargo e excluir", () -> {
            Cenario c = new Cenario();
            Funcionario novo = Cenario.funcionario(Cargo.ATENDENTE);
            c.locadora.incluirFuncionario(novo);
            igual(3, c.locadora.getFuncionarios().listar().size());
            igual(novo, c.locadora.getFuncionarios().buscarPorMatricula(novo.getMatricula().toLowerCase()));

            novo.setCargo(Cargo.GERENTE);
            c.locadora.alterarFuncionario(novo);
            igual(Cargo.GERENTE, novo.getCargo());

            c.locadora.excluirFuncionario(novo.getId());
            igual(2, c.locadora.getFuncionarios().listar().size());
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getFuncionarios().buscarPorMatricula(novo.getMatricula()));
        });
        caso("CT64", "item i", "Buscar por código, placa, CPF ou matrícula inexistentes lança RegistroNaoEncontradoException", () -> {
            Cenario c = new Cenario();
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getVeiculos().buscarPorId(-1));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getVeiculos().buscarPorPlaca("AAA0A00"));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getClientes().buscarPorCpf("00000000000"));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getFuncionarios().buscarPorMatricula("NAOEXISTE"));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.getLocacoes().buscarPorId(-1));
        });
    }
}
