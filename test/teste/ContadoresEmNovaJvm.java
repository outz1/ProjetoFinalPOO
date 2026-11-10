package teste;

import model.Cliente;
import model.Locacao;
import model.Veiculo;
import persistence.ArquivoDados;
import service.Locadora;

/** Roda numa JVM separada (ver CT77): carrega o arquivo e mostra os próximos códigos de veículo, pessoa e locação. */
public final class ContadoresEmNovaJvm {
    private ContadoresEmNovaJvm() {
    }

    public static void main(String[] args) throws Exception {
        Locadora locadora = new Locadora(new ArquivoDados(args[0]));
        locadora.carregarDados();
        Veiculo veiculo = Cenario.carro(100, false);
        Cliente cliente = Cenario.clienteHabilitado();
        Locacao locacao = new Locacao(cliente, veiculo, locadora.getFuncionarios().listar().get(0),
                Cenario.RETIRADA, Cenario.dia(1), false);
        System.out.println(veiculo.getId() + " " + cliente.getId() + " " + locacao.getId());
    }
}
