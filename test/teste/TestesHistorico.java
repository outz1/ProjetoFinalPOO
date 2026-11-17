package teste;

import exception.DadoDuplicadoException;
import model.Alteracao;
import model.CategoriaAlteracao;
import model.Cliente;
import model.Locacao;
import model.SituacaoVeiculo;
import model.TipoAlteracao;
import model.Veiculo;
import persistence.ArquivoDados;
import service.Locadora;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import static teste.Cenario.dia;
import static teste.Verificador.caso;
import static teste.Verificador.grupo;
import static teste.Verificador.igual;
import static teste.Verificador.lanca;
import static teste.Verificador.verdadeiro;

/** Historico de alteracoes que alimenta o painel de alteracoes do menu principal */
final class TestesHistorico {
    // TODO: classe utilitaria nao instanciavel
    private TestesHistorico() {
    }

    // TODO: devolve a ultima alteracao anotada na categoria
    private static Alteracao ultima(Locadora locadora, CategoriaAlteracao categoria) {
        List<Alteracao> itens = locadora.getHistorico().listarPorCategoria(categoria);
        return itens.get(itens.size() - 1);
    }

    // TODO: executa os casos do historico de alteracoes
    static void executar() {
        grupo("Histórico de alterações: painel do menu principal");
        caso("CT72", "painel", "Incluir veículo, cliente e funcionário anota uma inclusão na categoria certa", () -> {
            Cenario c = new Cenario();
            // o cenario ja incluiu o atendente e o gerente
            igual(2, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.FUNCIONARIOS).size());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Alteracao doVeiculo = ultima(c.locadora, CategoriaAlteracao.VEICULOS);
            igual(TipoAlteracao.INCLUSAO, doVeiculo.getTipo());
            verdadeiro(doVeiculo.getRegistro().contains(carro.getPlaca()), "registro: " + doVeiculo.getRegistro());
            igual(TipoAlteracao.INCLUSAO, ultima(c.locadora, CategoriaAlteracao.CLIENTES).getTipo());
            verdadeiro(ultima(c.locadora, CategoriaAlteracao.CLIENTES).getDetalhes().contains(cliente.getCpf()), "detalhes com o CPF");
            igual(1, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.VEICULOS).size());
            igual(0, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.LOCACOES).size());
        });
        caso("CT73", "painel", "Alterar um veículo anota só o campo que mudou; salvar sem mudar nada não anota", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            c.locadora.alterarVeiculo(carro);
            igual(1, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.VEICULOS).size());

            String corAntes = carro.getCor();
            carro.setCor("Verde-limão");
            c.locadora.alterarVeiculo(carro);
            Alteracao alteracao = ultima(c.locadora, CategoriaAlteracao.VEICULOS);
            igual(TipoAlteracao.ALTERACAO, alteracao.getTipo());
            igual("Cor " + corAntes + "  →  Cor Verde-limão", alteracao.getDetalhes());

            // a foto foi renovada no salvamento: uma segunda alteracao nao repete a primeira
            carro.setQuilometragem(carro.getQuilometragem() + 100);
            c.locadora.alterarVeiculo(carro);
            verdadeiro(!ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes().contains("Verde-limão"), "só o km mudou");
            igual(3, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.VEICULOS).size());
        });
        caso("CT74", "painel", "Alteração recusada (placa repetida) não entra no histórico", () -> {
            Cenario c = new Cenario();
            Veiculo a = c.cadastrar(Cenario.carro(100, false));
            Veiculo b = c.cadastrar(Cenario.carro(100, false));
            int antes = c.locadora.getHistorico().listar().size();
            b.setPlaca(a.getPlaca());
            lanca(DadoDuplicadoException.class, () -> c.locadora.alterarVeiculo(b));
            igual(antes, c.locadora.getHistorico().listar().size());
        });
        caso("CT75", "painel", "Excluir anota a exclusão com os dados que o registro tinha", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            c.locadora.excluirVeiculo(carro.getId());
            Alteracao exclusao = ultima(c.locadora, CategoriaAlteracao.VEICULOS);
            igual(TipoAlteracao.EXCLUSAO, exclusao.getTipo());
            verdadeiro(exclusao.getDetalhes().contains(carro.getPlaca()), "detalhes com a placa");
        });
        caso("CT76", "painel", "Manutenção e retorno anotam a mudança de situação do veículo", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            c.locadora.enviarParaManutencao(carro.getId());
            igual(SituacaoVeiculo.DISPONIVEL + "  →  " + SituacaoVeiculo.MANUTENCAO,
                    ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes());
            c.locadora.retornarDaManutencao(carro.getId());
            igual(SituacaoVeiculo.MANUTENCAO + "  →  " + SituacaoVeiculo.DISPONIVEL,
                    ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes());
        });
        caso("CT77", "painel", "Locar, devolver e cancelar anotam a locação e a situação do veículo", () -> {
            Cenario c = new Cenario();
            Cliente cliente = c.cadastrar(Cenario.clienteHabilitado());
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            Locacao l = c.locar(cliente, carro, dia(3), false);
            igual(TipoAlteracao.INCLUSAO, ultima(c.locadora, CategoriaAlteracao.LOCACOES).getTipo());
            verdadeiro(ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes().contains(SituacaoVeiculo.LOCADO.toString()),
                    "veículo ficou locado");

            c.locadora.registrarDevolucao(l.getId(), dia(3), carro.getQuilometragem() + 50);
            Alteracao devolucao = ultima(c.locadora, CategoriaAlteracao.LOCACOES);
            igual(TipoAlteracao.ALTERACAO, devolucao.getTipo());
            igual("Devolução registrada", devolucao.getAcao());
            verdadeiro(devolucao.getDetalhes().contains("Devolvida"), "mostra a data de devolução: " + devolucao.getDetalhes());
            verdadeiro(ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes().contains(SituacaoVeiculo.DISPONIVEL.toString()),
                    "veículo voltou para a frota");

            Locacao outra = c.locar(cliente, carro, dia(2), false);
            c.locadora.cancelarLocacao(outra.getId());
            igual("Locação cancelada", ultima(c.locadora, CategoriaAlteracao.LOCACOES).getAcao());
            igual(4, c.locadora.getHistorico().listarPorCategoria(CategoriaAlteracao.LOCACOES).size());
        });
        caso("CT78", "painel, RF13", "O histórico é gravado no arquivo e uma locadora nova o enxerga", () -> {
            Cenario c = new Cenario();
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            carro.setCor("Azul");
            c.locadora.alterarVeiculo(carro);
            Locadora lida = new Locadora(new ArquivoDados(c.arquivo().toString()));
            lida.carregarDados();
            igual(c.locadora.getHistorico().listar().size(), lida.getHistorico().listar().size());
            igual(ultima(c.locadora, CategoriaAlteracao.VEICULOS).getDetalhes(), ultima(lida, CategoriaAlteracao.VEICULOS).getDetalhes());

            // a foto e refeita ao carregar: alterar depois de carregar anota so a mudanca nova
            Veiculo lido = lida.getVeiculos().buscarPorId(carro.getId());
            lido.setCor("Preto");
            lida.alterarVeiculo(lido);
            igual("Cor Azul  →  Cor Preto", ultima(lida, CategoriaAlteracao.VEICULOS).getDetalhes());
        });
        caso("CT79", "painel", "O histórico avisa o ouvinte a cada alteração anotada e para de avisar depois de removido", () -> {
            Cenario c = new Cenario();
            int[] avisos = {0};
            Runnable ouvinte = () -> avisos[0]++;
            c.locadora.getHistorico().adicionarOuvinte(ouvinte);
            Veiculo carro = c.cadastrar(Cenario.carro(100, false));
            igual(1, avisos[0]);
            carro.setCor("Rosa");
            c.locadora.alterarVeiculo(carro);
            igual(2, avisos[0]);
            c.locadora.alterarVeiculo(carro);
            igual(2, avisos[0]);
            c.locadora.getHistorico().removerOuvinte(ouvinte);
            c.locadora.enviarParaManutencao(carro.getId());
            igual(2, avisos[0]);
        });
        caso("CT80", "painel, RF13", "Arquivo gravado antes do histórico existir ainda carrega, com histórico vazio", () -> {
            Cenario c = new Cenario();
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(c.arquivo()))) {
                for (int i = 0; i < 4; i++)
                    saida.writeObject(new ArrayList<>());
            }
            Locadora lida = new Locadora(new ArquivoDados(c.arquivo().toString()));
            lida.carregarDados();
            igual(0, lida.getHistorico().listar().size());
            igual(0, lida.getVeiculos().listar().size());
        });
    }
}
