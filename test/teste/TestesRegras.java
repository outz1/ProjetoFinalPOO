package teste;

import exception.DadoDuplicadoException;
import exception.LocadoraException;
import exception.HabilitacaoInvalidaException;
import exception.LocacaoAtivaExistenteException;
import exception.RegistroNaoEncontradoException;
import exception.VeiculoIndisponivelException;
import model.Carro;
import model.Cliente;
import model.Funcionario;
import model.Cargo;
import model.Locacao;
import model.SituacaoLocacao;
import model.SituacaoVeiculo;
import model.Veiculo;
import java.time.LocalDate;
import java.util.List;
import static teste.Cenario.RETIRADA;
import static teste.Cenario.dia;
import static teste.Verificador.caso;
import static teste.Verificador.grupo;
import static teste.Verificador.igual;
import static teste.Verificador.lanca;
import static teste.Verificador.lancaExata;
import static teste.Verificador.verdadeiro;

/** Regras de negócio que envolvem várias entidades, exercitadas pela classe Locadora. */
final class TestesRegras {
    private TestesRegras() {
    }

    static void executar() {
        locacao();
        habilitacao();
        exclusaoEDuplicidade();
        devolucaoECancelamento();
        consultas();
    }

    private static void locacao() {
        grupo("Locadora: registrar locação (UC04, UC05, UC06)");
        caso("CT31", "RF05, UC04", "Locação válida fica Ativa, o veículo fica Locado e o valor previsto é calculado", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(10), true);
            igual(SituacaoLocacao.ATIVA, l.getSituacao());
            igual(SituacaoVeiculo.LOCADO, carro.getSituacao());
            igual(1817.50, l.getValorPrevisto());
            igual(c.atendente.getId(), l.getFuncionario().getId());
            igual(1, c.locadora.getLocacoes().listar().size());
        });
        caso("CT32", "RF07, UC06", "simularLocacao mostra o valor previsto sem gravar nada", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            igual(1817.50, c.locadora.simularLocacao(cliente.getId(), carro.getId(), RETIRADA, dia(10), true));
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            igual(0, c.locadora.getLocacoes().listar().size());
        });
        caso("CT33", "RN01, RF06", "Veículo Locado ou em manutenção não pode ser locado e nada é gravado", () -> {
            Cenario c = new Cenario();
            Cliente a = c.cadastrar(Cenario.clienteHabilitado());
            Cliente b = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            c.locar(a, carro, dia(3), false);
            lanca(VeiculoIndisponivelException.class, () -> c.locar(b, carro, dia(3), false));

            Veiculo outro = c.cadastrar(Cenario.carro(100, false));
            c.locadora.enviarParaManutencao(outro.getId());
            lanca(VeiculoIndisponivelException.class, () -> c.locar(b, outro, dia(3), false));
            igual(1, c.locadora.getLocacoes().listar().size());
        });
        caso("CT34", "RN04", "Cliente com locação ativa não pode alugar outro veículo; depois de devolver, pode", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo a = c.cadastrar(Cenario.carro(100, false));
            Veiculo b = c.cadastrar(Cenario.carro(100, false));
            Locacao primeira = c.locar(cliente, a, dia(3), false);
            lanca(LocacaoAtivaExistenteException.class, () -> c.locar(cliente, b, dia(3), false));
            igual(SituacaoVeiculo.DISPONIVEL, b.getSituacao());
            c.locadora.registrarDevolucao(primeira.getId(), dia(3), 1500);
            c.locar(cliente, b, dia(3), false);
        });
        caso("CT35", "RN05", "Datas inválidas na locação são recusadas e nada é gravado", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            lancaExata(LocadoraException.class, () -> c.locar(cliente, carro, RETIRADA, false));
            lancaExata(LocadoraException.class, () -> c.locar(cliente, carro, dia(-1), false));
            lancaExata(LocadoraException.class, () -> c.locadora.realizarLocacao(cliente.getId(), carro.getId(),
                    c.atendente.getId(), null, dia(3), false));
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            igual(0, c.locadora.getLocacoes().listar().size());
        });
        caso("CT36", "RF05", "Cliente, veículo ou funcionário inexistente: registro não encontrado", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.realizarLocacao(-1, carro.getId(), c.atendente.getId(), RETIRADA, dia(2), false));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.realizarLocacao(cliente.getId(), -1, c.atendente.getId(), RETIRADA, dia(2), false));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.realizarLocacao(cliente.getId(), carro.getId(), -1, RETIRADA, dia(2), false));
        });
    }

    private static void habilitacao() {
        grupo("Locadora: habilitação do cliente (RN02, RN03)");
        caso("CT37", "RN02", "Idade mínima de 21 anos na retirada: 21 anos exatos passa, 20 anos e 364 dias não", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Cliente vinteEUm = c.cadastrar(Cenario.cliente("B", RETIRADA.minusYears(21), Cenario.VALIDADE_CNH));
            Cliente vinte = c.cadastrar(Cenario.cliente("B", RETIRADA.minusYears(21).plusDays(1), Cenario.VALIDADE_CNH));
            HabilitacaoInvalidaException erro = lanca(HabilitacaoInvalidaException.class, () -> c.locar(vinte, carro, dia(3), false));
            verdadeiro(erro.getMessage().contains("21"), "a mensagem deve citar a idade mínima: " + erro.getMessage());
            c.locar(vinteEUm, carro, dia(3), false);
        });
        caso("CT38", "RN02", "CNH vencida é recusada; CNH que vence no dia da retirada ainda vale", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Cliente vencida = c.cadastrar(Cenario.cliente("B", LocalDate.of(1990, 1, 1), RETIRADA.minusDays(1)));
            Cliente noDia = c.cadastrar(Cenario.cliente("B", LocalDate.of(1990, 1, 1), RETIRADA));
            lanca(HabilitacaoInvalidaException.class, () -> c.locar(vencida, carro, dia(3), false));
            c.locar(noDia, carro, dia(3), false);
        });
        caso("CT39", "RN03", "Moto exige CNH A: B é recusada; A e AB são aceitas", () -> {
            Cenario c = new Cenario();
            Cliente soB = c.cadastrar(Cenario.cliente("B", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Cliente soA = c.cadastrar(Cenario.cliente("A", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Cliente ab = c.cadastrar(Cenario.cliente("AB", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Veiculo moto = c.cadastrar(Cenario.moto(80, 160));
            lanca(HabilitacaoInvalidaException.class, () -> c.locar(soB, moto, dia(2), false));
            c.locar(soA, moto, dia(2), false);
            c.locadora.cancelarLocacao(c.locadora.getLocacoes().listarAtivas().get(0).getId());
            c.locar(ab, moto, dia(2), false);
        });
        caso("CT40", "RN03", "Carro exige CNH B: só A é recusada", () -> {
            Cenario c = new Cenario();
            Cliente soA = c.cadastrar(Cenario.cliente("A", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            lanca(HabilitacaoInvalidaException.class, () -> c.locar(soA, carro, dia(2), false));
        });
        caso("CT41", "RN03", "Utilitário até 1.500 kg exige B; acima disso exige C", () -> {
            Cenario c = new Cenario();
            Cliente soB = c.cadastrar(Cenario.cliente("B", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Cliente soC = c.cadastrar(Cenario.cliente("C", LocalDate.of(1990, 1, 1), Cenario.VALIDADE_CNH));
            Veiculo leve = c.cadastrar(Cenario.utilitario(100, 1500));
            Veiculo pesado = c.cadastrar(Cenario.utilitario(200, 1800));
            c.locar(soB, leve, dia(2), false);
            lanca(HabilitacaoInvalidaException.class, () -> c.locar(soB, pesado, dia(2), false));
            c.locar(soC, pesado, dia(2), false);
        });
    }

    private static void exclusaoEDuplicidade() {
        grupo("Locadora: exclusão e dados duplicados (RN09, RN10)");
        caso("CT42", "RN09", "Veículo com locação ativa não pode ser excluído; depois da devolução pode", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(2), false);
            lancaExata(LocadoraException.class, () -> c.locadora.excluirVeiculo(carro.getId()));
            verdadeiro(c.locadora.getVeiculos().listar().contains(carro), "o veículo deve continuar cadastrado");
            c.locadora.registrarDevolucao(l.getId(), dia(2), 1200);
            c.locadora.excluirVeiculo(carro.getId());
            igual(0, c.locadora.getVeiculos().listar().size());
        });
        caso("CT43", "RN09", "Cliente com locação ativa não pode ser excluído; locação cancelada libera a exclusão", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(2), false);
            lancaExata(LocadoraException.class, () -> c.locadora.excluirCliente(cliente.getId()));
            c.locadora.cancelarLocacao(l.getId());
            c.locadora.excluirCliente(cliente.getId());
            igual(0, c.locadora.getClientes().listar().size());
        });
        caso("CT44", "RN10", "Placa repetida é recusada na inclusão, sem diferenciar maiúsculas de minúsculas", () -> {
            Cenario c = new Cenario();
            Carro original = (Carro) c.cadastrar(Cenario.carro(100, false));
            lanca(DadoDuplicadoException.class, () -> c.locadora.incluirVeiculo(
                    new Carro(original.getPlaca().toLowerCase(),
                            "VW", "Gol", 2020, "", 0, 90, 4, false, false)));
            igual(1, c.locadora.getVeiculos().listar().size());
        });
        caso("CT45", "RN10", "CPF repetido de cliente é recusado", () -> {
            Cenario c = new Cenario();
            Cliente original = c.cadastrar(Cenario.clienteHabilitado());
            lanca(DadoDuplicadoException.class, () -> c.locadora.incluirCliente(new Cliente("Outro", original.getCpf(), "", "",
                    LocalDate.of(1990, 1, 1), "1", "B", Cenario.VALIDADE_CNH, "")));
            igual(1, c.locadora.getClientes().listar().size());
        });
        caso("CT46", "RN10", "Funcionário: CPF e matrícula repetidos são recusados", () -> {
            Cenario c = new Cenario();
            Funcionario base = c.atendente;
            lanca(DadoDuplicadoException.class, () -> c.locadora.incluirFuncionario(new Funcionario("Outro", base.getCpf(), "", "",
                    LocalDate.of(1990, 1, 1), "ZZZ", Cargo.ATENDENTE)));
            lanca(DadoDuplicadoException.class, () -> c.locadora.incluirFuncionario(new Funcionario("Outro", Cenario.cpf(), "", "",
                    LocalDate.of(1990, 1, 1), base.getMatricula().toLowerCase(), Cargo.ATENDENTE)));
            igual(2, c.locadora.getFuncionarios().listar().size());
        });
    }

    private static void devolucaoECancelamento() {
        grupo("Locadora: devolução, multa e cancelamento (UC07, UC08, UC09)");
        caso("CT47", "RF08, UC07", "Devolução no prazo: valor final igual ao previsto, locação Finalizada, veículo Disponível", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(10), true);
            igual(1817.50, c.locadora.registrarDevolucao(l.getId(), dia(10), 1900));
            igual(SituacaoLocacao.FINALIZADA, l.getSituacao());
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            igual(1900, carro.getQuilometragem());
        });
        caso("CT48", "RN08, UC08", "Devolução com 2 dias de atraso soma a multa: R$ 2.312,50 (exemplo do PDF)", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(10), true);
            igual(2312.50, c.locadora.registrarDevolucao(l.getId(), dia(12), 2100));
            igual(2312.50, l.getValorFinal());
        });
        caso("CT49", "UC07", "Devolução: locação inexistente, já finalizada ou km menor que o da retirada são recusadas", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(5), false);
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.registrarDevolucao(-1, dia(5), 2000));
            lancaExata(LocadoraException.class, () -> c.locadora.registrarDevolucao(l.getId(), dia(5), 10));
            c.locadora.registrarDevolucao(l.getId(), dia(5), 1500);
            lancaExata(LocadoraException.class, () -> c.locadora.registrarDevolucao(l.getId(), dia(5), 1600));
        });
        caso("CT50", "RF09, RN09", "Cancelar libera o veículo, não cobra nada e o cliente pode alugar de novo", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(5), true);
            c.locadora.cancelarLocacao(l.getId());
            igual(SituacaoLocacao.CANCELADA, l.getSituacao());
            igual(0.00, l.getValorFinal());
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            lancaExata(LocadoraException.class, () -> c.locadora.cancelarLocacao(l.getId()));
            c.locar(cliente, carro, dia(5), false);
        });
        caso("CT51", "UC07", "A locação é localizada pelo código ou pela placa do veículo locado", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(150, true));
            Veiculo parado = c.cadastrar(Cenario.carro(150, true));
            Locacao l = c.locar(cliente, carro, dia(5), false);
            igual(l, c.locadora.localizarLocacaoAtiva(String.valueOf(l.getId())));
            igual(l, c.locadora.localizarLocacaoAtiva(carro.getPlaca().toLowerCase()));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.localizarLocacaoAtiva(parado.getPlaca()));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.localizarLocacaoAtiva("999999"));
        });
    }

    private static void consultas() {
        grupo("Locadora: manutenção e consultas (UC01, UC10, UC11)");
        caso("CT52", "RF02", "Manutenção: envia e retorna à frota; só volta quem está em manutenção; locado não vai", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            lancaExata(LocadoraException.class, () -> c.locadora.retornarDaManutencao(carro.getId()));
            c.locadora.enviarParaManutencao(carro.getId());
            igual(SituacaoVeiculo.MANUTENCAO, carro.getSituacao());
            c.locadora.retornarDaManutencao(carro.getId());
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            c.locar(cliente, carro, dia(2), false);
            lancaExata(LocadoraException.class, () -> c.locadora.enviarParaManutencao(carro.getId()));
            lancaExata(LocadoraException.class, () -> c.locadora.retornarDaManutencao(carro.getId()));
        });
        caso("CT53", "RF10, UC10", "Frota disponível exclui Locados e em manutenção e filtra por categoria", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo livre = c.cadastrar(Cenario.carro(100, false));
            Veiculo locado = c.cadastrar(Cenario.carro(100, false));
            Veiculo oficina = c.cadastrar(Cenario.carro(100, false));
            Veiculo moto = c.cadastrar(Cenario.moto(80, 160));
            Veiculo utilitario = c.cadastrar(Cenario.utilitario(100, 800));
            c.locar(cliente, locado, dia(2), false);
            c.locadora.enviarParaManutencao(oficina.getId());

            List<Veiculo> todos = c.locadora.listarDisponiveis(null);
            igual(3, todos.size());
            verdadeiro(todos.contains(livre) && todos.contains(moto) && todos.contains(utilitario), "livre, moto e utilitário");
            igual(List.of(livre), c.locadora.listarDisponiveis("Carro"));
            igual(List.of(moto), c.locadora.listarDisponiveis("moto"));
            igual(List.of(utilitario), c.locadora.listarDisponiveis("Utilitário"));
            igual(3, c.locadora.listarDisponiveis("").size());
        });
        caso("CT54", "RF11, UC11", "Histórico por cliente e por veículo traz locações em qualquer situação", () -> {
            Cenario c = new Cenario();
            Cliente ana = c.cadastrar(Cenario.clienteHabilitado());
            Cliente beto = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Veiculo outro = c.cadastrar(Cenario.carro(100, false));
            Locacao primeira = c.locar(ana, carro, dia(2), false);
            c.locadora.registrarDevolucao(primeira.getId(), dia(2), 1100);
            Locacao segunda = c.locar(ana, carro, dia(4), false);
            c.locadora.cancelarLocacao(segunda.getId());
            Locacao terceira = c.locar(beto, carro, dia(3), false);
            c.locar(ana, outro, dia(3), false);

            igual(3, c.locadora.historicoPorCliente(ana.getCpf()).size());
            igual(List.of(terceira), c.locadora.historicoPorCliente(beto.getCpf()));
            igual(List.of(primeira, segunda, terceira), c.locadora.historicoPorVeiculo(carro.getPlaca()));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.historicoPorCliente("00000000000"));
            lanca(RegistroNaoEncontradoException.class, () -> c.locadora.historicoPorVeiculo("ZZZ9999"));
        });
        caso("CT55", "RF11, UC11", "Locações em atraso: só as ativas depois da data prevista", () -> {
            Cenario c = new Cenario();
            Cliente ana = c.cadastrar(Cenario.clienteHabilitado());
            Cliente beto = c.cadastrar(Cenario.clienteHabilitado());
            Cliente caio = c.cadastrar(Cenario.clienteHabilitado());
            Locacao curta = c.locar(ana, c.cadastrar(Cenario.carro(100, false)), dia(2), false);
            c.locar(beto, c.cadastrar(Cenario.carro(100, false)), dia(10), false);
            Locacao devolvida = c.locar(caio, c.cadastrar(Cenario.carro(100, false)), dia(2), false);
            c.locadora.registrarDevolucao(devolvida.getId(), dia(2), 1100);

            igual(0, c.locadora.listarAtrasadas(dia(2)).size());
            igual(List.of(curta), c.locadora.listarAtrasadas(dia(3)));
            igual(2, c.locadora.listarAtrasadas(dia(11)).size());
        });
    }
}
