package teste;

import exception.LocadoraException;
import exception.VeiculoIndisponivelException;
import model.Cargo;
import model.Carro;
import model.Cliente;
import model.Funcionario;
import model.Identificavel;
import model.Locacao;
import model.Moto;
import model.Pessoa;
import model.SituacaoLocacao;
import model.SituacaoVeiculo;
import model.Tarifavel;
import model.Veiculo;
import repository.Repositorio;
import repository.RepositorioBase;
import java.lang.reflect.Modifier;
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

/** Classes do modelo: diárias, fórmulas de valor, estados e validações de campos. */
final class TestesModelo {
    private TestesModelo() {
    }

    private static Locacao locacao(Veiculo veiculo, LocalDate prevista, boolean seguro) throws Exception {
        return new Locacao(Cenario.clienteHabilitado(), veiculo, Cenario.funcionario(Cargo.ATENDENTE), RETIRADA, prevista, seguro);
    }

    static void executar() {
        diarias();
        valores();
        estados();
        identificadores();
        estrutura();
    }

    private static void diarias() {
        grupo("Modelo: diária e CNH de cada categoria (polimorfismo)");
        caso("CT01", "RF01", "Carro automático: diária base +10% (150,00 vira 165,00)",
                () -> igual(165.00, Cenario.carro(150, true).calcularValorDiaria()));
        caso("CT02", "RF01", "Carro manual: diária igual à base",
                () -> igual(150.00, Cenario.carro(150, false).calcularValorDiaria()));
        caso("CT03", "RF01", "Moto: +20% só acima de 500 cc (500 cc paga a base, 600 cc paga 120,00)", () -> {
            igual(100.00, Cenario.moto(100, 500).calcularValorDiaria());
            igual(120.00, Cenario.moto(100, 600).calcularValorDiaria());
            igual(100.00, Cenario.moto(100, 160).calcularValorDiaria());
        });
        caso("CT04", "RF01", "Utilitário: diária base + R$ 0,02 por kg (200,00 e 1000 kg viram 220,00)",
                () -> igual(220.00, Cenario.utilitario(200, 1000).calcularValorDiaria()));
        caso("CT05", "RN03", "CNH exigida: Moto A, Carro B, Utilitário B até 1.500 kg e C acima", () -> {
            igual("A", Cenario.moto(100, 160).getCnhExigida());
            igual("B", Cenario.carro(100, false).getCnhExigida());
            igual("B", Cenario.utilitario(100, 1500).getCnhExigida());
            igual("C", Cenario.utilitario(100, 1500.5).getCnhExigida());
        });
        caso("CT06", "RF01", "Cada subclasse informa a própria categoria e a diária é calculada sem conhecer o tipo", () -> {
            List<Veiculo> frota = List.of(Cenario.carro(100, false), Cenario.moto(100, 160), Cenario.utilitario(100, 500));
            igual("Carro", frota.get(0).getCategoria());
            igual("Moto", frota.get(1).getCategoria());
            igual("Utilitário", frota.get(2).getCategoria());
            Tarifavel utilitario = frota.get(2);
            igual(110.00, utilitario.calcularValorDiaria());
        });
    }

    private static void valores() {
        grupo("Modelo: valor da locação, desconto, seguro e multa");
        caso("CT07", "RN06", "Desconto por período: 0% até 6, 5% de 7 a 14, 10% de 15 a 29, 15% a partir de 30", () -> {
            igual(0.00, Locacao.descontoPorDiarias(1));
            igual(0.00, Locacao.descontoPorDiarias(6));
            igual(0.05, Locacao.descontoPorDiarias(7));
            igual(0.05, Locacao.descontoPorDiarias(14));
            igual(0.10, Locacao.descontoPorDiarias(15));
            igual(0.10, Locacao.descontoPorDiarias(29));
            igual(0.15, Locacao.descontoPorDiarias(30));
            igual(0.15, Locacao.descontoPorDiarias(90));
        });
        caso("CT08", "RF07, RN06, RN07", "Exemplo do PDF: carro automático (base 150), 10 diárias com seguro = R$ 1.817,50", () -> {
            Locacao l = locacao(Cenario.carro(150, true), dia(10), true);
            igual(165.00, l.getVeiculo().calcularValorDiaria());
            igual(1817.50, l.getValorPrevisto());
        });
        caso("CT09", "RF07, RN07", "Seguro é opcional: o mesmo aluguel sem seguro custa 1.567,50", () ->
                igual(1567.50, locacao(Cenario.carro(150, true), dia(10), false).getValorPrevisto()));
        caso("CT10", "RF08, RN08", "Exemplo do PDF: 2 dias de atraso somam 495,00 de multa e o total é R$ 2.312,50", () -> {
            Veiculo carro = Cenario.carro(150, true);
            Locacao l = locacao(carro, dia(10), true);
            igual(495.00, l.calcularMulta(dia(12)));
            carro.locar();
            l.finalizar(dia(12), 2000);
            igual(2312.50, l.getValorFinal());
        });
        caso("CT11", "RF08", "Devolução no prazo não tem multa", () -> {
            Locacao l = locacao(Cenario.carro(150, true), dia(10), true);
            igual(0.00, l.calcularMulta(dia(10)));
            igual(0.00, l.calcularMulta(dia(3)));
        });
        caso("CT12", "UC07", "Devolução antecipada recalcula pelas diárias usadas (8 de 10: 8 x 165 x 0,95 + 200 = 1.454,00)", () -> {
            Veiculo carro = Cenario.carro(150, true);
            Locacao l = locacao(carro, dia(10), true);
            carro.locar();
            l.finalizar(dia(8), 2000);
            igual(1454.00, l.getValorFinal());
        });
        caso("CT13", "RN05", "Devolução no mesmo dia da retirada cobra o mínimo de uma diária", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(3), false);
            carro.locar();
            l.finalizar(dia(0), 1000);
            igual(150.00, l.getValorFinal());
        });
        caso("CT14", "RN05", "A devolução prevista tem de ser depois da retirada (igual ou antes é recusado)", () -> {
            Veiculo carro = Cenario.carro(150, false);
            lancaExata(LocadoraException.class, () -> locacao(carro, RETIRADA, false));
            lancaExata(LocadoraException.class, () -> locacao(carro, dia(-2), false));
        });
    }

    private static void estados() {
        grupo("Modelo: situação do veículo e da locação");
        caso("CT15", "RN01", "locar() muda para Locado e um segundo locar() é recusado", () -> {
            Veiculo carro = Cenario.carro(100, false);
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            carro.locar();
            igual(SituacaoVeiculo.LOCADO, carro.getSituacao());
            lanca(VeiculoIndisponivelException.class, carro::locar);
        });
        caso("CT16", "RF02", "Manutenção: veículo disponível vai e volta; veículo locado não pode ir", () -> {
            Veiculo carro = Cenario.carro(100, false);
            carro.enviarParaManutencao();
            igual(SituacaoVeiculo.MANUTENCAO, carro.getSituacao());
            lanca(VeiculoIndisponivelException.class, carro::locar);
            carro.liberar();
            carro.locar();
            lancaExata(LocadoraException.class, carro::enviarParaManutencao);
        });
        caso("CT17", "RF08", "finalizar() encerra a locação, grava data e km e libera o veículo", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), false);
            carro.locar();
            l.finalizar(dia(5), 1800);
            igual(SituacaoLocacao.FINALIZADA, l.getSituacao());
            igual(dia(5), l.getDataDevolucao());
            igual(1800, l.getKmDevolucao());
            igual(1800, carro.getQuilometragem());
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
        });
        caso("CT18", "UC07", "Quilometragem menor que a da retirada é recusada e nada muda", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), false);
            carro.locar();
            lancaExata(LocadoraException.class, () -> l.finalizar(dia(5), 999));
            igual(SituacaoLocacao.ATIVA, l.getSituacao());
            igual(SituacaoVeiculo.LOCADO, carro.getSituacao());
        });
        caso("CT19", "UC07", "Devolução antes da retirada é recusada", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), false);
            lancaExata(LocadoraException.class, () -> l.finalizar(dia(-1), 1500));
        });
        caso("CT20", "UC07", "Finalizar uma locação já finalizada é recusado", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), false);
            carro.locar();
            l.finalizar(dia(5), 1100);
            lancaExata(LocadoraException.class, () -> l.finalizar(dia(5), 1200));
        });
        caso("CT21", "RF09, RN09", "cancelar() zera o valor, libera o veículo e só vale para locação ativa", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), true);
            carro.locar();
            l.cancelar();
            igual(SituacaoLocacao.CANCELADA, l.getSituacao());
            igual(0.00, l.getValorFinal());
            igual(SituacaoVeiculo.DISPONIVEL, carro.getSituacao());
            lancaExata(LocadoraException.class, l::cancelar);
        });
        caso("CT22", "RF11", "isAtrasada: só a locação ativa depois da data prevista", () -> {
            Veiculo carro = Cenario.carro(150, false);
            Locacao l = locacao(carro, dia(5), false);
            verdadeiro(!l.isAtrasada(dia(5)), "no último dia ainda não está atrasada");
            verdadeiro(l.isAtrasada(dia(6)), "um dia depois está atrasada");
            carro.locar();
            l.finalizar(dia(7), 1100);
            verdadeiro(!l.isAtrasada(dia(30)), "locação finalizada não conta como atrasada");
        });
        caso("CT23", "RN02", "Cliente: idade na data, CNH válida até o dia da validade (inclusive) e categorias de uma CNH AB", () -> {
            Cliente c = Cenario.cliente("AB", LocalDate.of(2000, 11, 5), LocalDate.of(2026, 11, 5));
            igual(26, c.calcularIdade(LocalDate.of(2026, 11, 5)));
            igual(25, c.calcularIdade(LocalDate.of(2026, 11, 4)));
            verdadeiro(c.isCnhValida(LocalDate.of(2026, 11, 5)), "vale no dia da validade");
            verdadeiro(!c.isCnhValida(LocalDate.of(2026, 11, 6)), "vencida no dia seguinte");
            verdadeiro(c.possuiCategoria("A") && c.possuiCategoria("B"), "AB tem A e B");
            verdadeiro(!c.possuiCategoria("C"), "AB não tem C");
        });
    }

    private static void identificadores() {
        grupo("Modelo: códigos automáticos e sequenciais (atributos estáticos)");
        caso("CT24", "RF12", "Clientes e funcionários compartilham a sequência de pessoas, sempre de 1 em 1", () -> {
            Cliente a = Cenario.clienteHabilitado();
            Funcionario b = Cenario.funcionario(Cargo.ATENDENTE);
            Cliente c = Cenario.clienteHabilitado();
            igual(a.getId() + 1, b.getId());
            igual(b.getId() + 1, c.getId());
        });
        caso("CT25", "RF12", "Veículos de qualquer categoria usam a mesma sequência", () -> {
            Veiculo a = Cenario.carro(100, false);
            Veiculo b = Cenario.moto(100, 160);
            Veiculo c = Cenario.utilitario(100, 500);
            igual(a.getId() + 1, b.getId());
            igual(b.getId() + 1, c.getId());
        });
        caso("CT26", "RF12", "Locações têm a própria sequência", () -> {
            Locacao a = locacao(Cenario.carro(100, false), dia(2), false);
            Locacao b = locacao(Cenario.carro(100, false), dia(2), false);
            igual(a.getId() + 1, b.getId());
        });
        caso("CT27", "RF12, RF13", "atualizarContador reajusta a sequência depois de carregar o arquivo e nunca a faz voltar", () -> {
            Veiculo.atualizarContador(5000);
            igual(5001, Cenario.carro(100, false).getId());
            Veiculo.atualizarContador(10);
            igual(5002, Cenario.carro(100, false).getId());
            Pessoa.atualizarContador(7000);
            igual(7001, Cenario.clienteHabilitado().getId());
            Locacao.atualizarContador(9000);
            igual(9001, locacao(Cenario.carro(100, false), dia(1), false).getId());
        });
        caso("CT28", "RF12, RN05", "Locação recusada por data inválida não consome código", () -> {
            Veiculo carro = Cenario.carro(100, false);
            Locacao antes = locacao(carro, dia(2), false);
            lancaExata(LocadoraException.class, () -> locacao(carro, RETIRADA, false));
            igual(antes.getId() + 1, locacao(carro, dia(2), false).getId());
        });
    }

    private static void estrutura() {
        grupo("Modelo: classes abstratas e interfaces");
        caso("CT29", "Item h", "Pessoa, Veiculo e RepositorioBase são abstratas; Identificavel, Tarifavel e Repositorio são interfaces", () -> {
            verdadeiro(Modifier.isAbstract(Pessoa.class.getModifiers()), "Pessoa abstrata");
            verdadeiro(Modifier.isAbstract(Veiculo.class.getModifiers()), "Veiculo abstrata");
            verdadeiro(Modifier.isAbstract(RepositorioBase.class.getModifiers()), "RepositorioBase abstrata");
            verdadeiro(Identificavel.class.isInterface() && Tarifavel.class.isInterface() && Repositorio.class.isInterface(), "interfaces");
        });
        caso("CT30", "Item h", "Todo objeto do modelo é Identificavel e serializável; veículo é Tarifavel", () -> {
            verdadeiro(Identificavel.class.isAssignableFrom(Cliente.class), "Cliente");
            verdadeiro(Identificavel.class.isAssignableFrom(Funcionario.class), "Funcionario");
            verdadeiro(Identificavel.class.isAssignableFrom(Veiculo.class), "Veiculo");
            verdadeiro(Identificavel.class.isAssignableFrom(Locacao.class), "Locacao");
            verdadeiro(java.io.Serializable.class.isAssignableFrom(Identificavel.class), "Serializable");
            verdadeiro(Tarifavel.class.isAssignableFrom(Veiculo.class), "Tarifavel");
        });
    }
}
