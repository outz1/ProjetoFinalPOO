package view;

import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import model.Cliente;
import model.Formato;
import model.Funcionario;
import model.Locacao;
import model.Veiculo;
import service.Locadora;
import java.time.LocalDate;
import java.util.List;

/** UC04 UC07 UC09 UC10 e UC11 o dia a dia do balcao */
public class TelaLocacoes extends Tela {
    private static final String NOVA = "Nova locação";
    private static final String DEVOLUCAO = "Registrar devolução";
    private static final String CANCELAR = "Cancelar locação";
    private static final String FROTA = "Consultar frota disponível";
    private static final String HIST_CLIENTE = "Histórico por cliente";
    private static final String HIST_VEICULO = "Histórico por veículo";
    private static final String ATRASO = "Locações em atraso";
    private static final String LISTAR = "Listar todas as locações";

    // TODO: cria a tela de locacoes e consultas
    public TelaLocacoes(Locadora locadora) {
        super(locadora);
    }

    // TODO: devolve o titulo da tela
    @Override
    protected String titulo() {
        return "Locações e consultas";
    }

    // TODO: devolve as opcoes do menu
    @Override
    protected String[] opcoes() {
        return new String[]{NOVA, DEVOLUCAO, CANCELAR, FROTA, HIST_CLIENTE, HIST_VEICULO, ATRASO, LISTAR};
    }

    // TODO: executa a opcao escolhida no menu
    @Override
    protected void executar(String opcao) throws LocadoraException {
        switch (opcao) {
            case NOVA -> novaLocacao();
            case DEVOLUCAO -> registrarDevolucao();
            case CANCELAR -> cancelarLocacao();
            case FROTA -> consultarFrota();
            case HIST_CLIENTE -> mostrarLista("Histórico do cliente", locadora.historicoPorCliente(lerTexto("CPF do cliente:")));
            case HIST_VEICULO -> mostrarLista("Histórico do veículo", locadora.historicoPorVeiculo(lerTexto("Placa do veículo:")));
            case ATRASO -> mostrarLista("Locações em atraso", locadora.listarAtrasadas(LocalDate.now()));
            case LISTAR -> mostrarLista("Todas as locações", locadora.getLocacoes().listar());
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }

    // TODO: informa o cliente escolhe o veiculo mostra o valor previsto e confirma UC04
    private void novaLocacao() throws LocadoraException {
        Funcionario funcionario = locadora.getFuncionarios().buscarPorMatricula(lerTexto("Matrícula do funcionário que está atendendo:"));
        Cliente cliente = locadora.getClientes().buscarPorCpf(lerTexto("CPF do cliente:"));

        List<Veiculo> disponiveis = locadora.listarDisponiveis(null);
        if (disponiveis.isEmpty()) {
            mostrarErro("Não há veículos disponíveis no momento.");
            return;
        }
        Veiculo veiculo = escolherItem("Veículo para " + cliente.getNome() + ":", disponiveis, TelaLocacoes::rotuloVeiculo);

        LocalDate retirada = lerData("Data de retirada", LocalDate.now());
        LocalDate prevista = lerData("Data prevista de devolução");
        boolean seguro = lerSimNao("Incluir seguro (R$ " + Formato.decimal(Locacao.TAXA_SEGURO) + " por diária)?");

        double previsto = locadora.simularLocacao(cliente.getId(), veiculo.getId(), retirada, prevista, seguro);
        long diarias = Locacao.diariasEntre(retirada, prevista);
        String resumo = "Cliente: " + cliente.getNome()
                + "\nVeículo: " + veiculo.getCategoria() + " " + veiculo.getPlaca() + " (" + veiculo.getModelo() + ")"
                + "\nPeríodo: " + Formato.data(retirada) + " a " + Formato.data(prevista) + " (" + diarias + " diária(s))"
                + "\nDiária: " + Formato.moeda(veiculo.calcularValorDiaria())
                + "\nDesconto: " + Math.round(Locacao.descontoPorDiarias(diarias) * 100) + "%"
                + "\nSeguro: " + (seguro ? "sim" : "não")
                + "\nValor previsto: " + Formato.moeda(previsto)
                + "\n\nConfirmar a locação?";
        if (!lerSimNao(resumo))
            return;

        Locacao locacao = locadora.realizarLocacao(cliente.getId(), veiculo.getId(), funcionario.getId(), retirada, prevista, seguro);
        mostrar("Locação " + locacao.getId() + " registrada. Valor previsto: " + Formato.moeda(locacao.getValorPrevisto()));
    }

    // TODO: recebe data e quilometragem e mostra o valor final com a multa se houver UC07
    private void registrarDevolucao() throws LocadoraException {
        Locacao locacao = locadora.localizarLocacaoAtiva(lerTexto("Código da locação ou placa do veículo:"));
        LocalDate devolucao = lerData("Data de devolução", LocalDate.now());
        int km = lerInteiro("Quilometragem na devolução:", locacao.getVeiculo().getQuilometragem());

        double total = locadora.registrarDevolucao(locacao.getId(), devolucao, km);
        long atraso = locacao.calcularDiasAtraso(devolucao);
        String texto = "Locação " + locacao.getId() + " finalizada."
                + "\nValor previsto: " + Formato.moeda(locacao.getValorPrevisto());
        if (atraso > 0)
            texto += "\nAtraso: " + atraso + " dia(s), multa de " + Formato.moeda(locacao.calcularMulta(devolucao));
        texto += "\nValor final: " + Formato.moeda(total);
        mostrar(texto);
    }

    // TODO: escolhe uma locacao ativa e a cancela se confirmado UC09
    private void cancelarLocacao() throws LocadoraException {
        List<Locacao> ativas = locadora.getLocacoes().listarAtivas();
        if (ativas.isEmpty())
            throw new RegistroNaoEncontradoException("locação ativa", "situação Ativa");
        Locacao locacao = escolherItem("Locação a cancelar:", ativas, TelaLocacoes::rotuloLocacao);
        if (lerSimNao("Cancelar a locação " + locacao.getId() + " sem cobrança?")) {
            locadora.cancelarLocacao(locacao.getId());
            mostrar("Locação cancelada. O veículo " + locacao.getVeiculo().getPlaca() + " voltou para a frota.");
        }
    }

    // TODO: resume o veiculo em uma linha para a lista de escolha
    private static String rotuloVeiculo(Veiculo v) {
        return v.getCategoria() + " " + v.getPlaca() + " | " + v.getMarca() + " " + v.getModelo() + " " + v.getAno()
                + " | " + Formato.moeda(v.calcularValorDiaria()) + "/dia";
    }

    // TODO: resume a locacao em uma linha para a lista de escolha
    private static String rotuloLocacao(Locacao l) {
        return "Locação " + l.getId() + " | " + l.getVeiculo().getPlaca() + " | " + l.getCliente().getNome()
                + " | prevista " + Formato.data(l.getDataPrevistaDevolucao());
    }

    // TODO: lista os veiculos disponiveis da categoria escolhida UC10
    private void consultarFrota() {
        String[] categorias = new String[Veiculo.CATEGORIAS.size() + 1];
        categorias[0] = "Todas";
        for (int i = 0; i < Veiculo.CATEGORIAS.size(); i++)
            categorias[i + 1] = Veiculo.CATEGORIAS.get(i);
        int escolha = lerOpcao("Categoria:", categorias);
        String categoria = escolha == 0 ? null : categorias[escolha];
        mostrarLista("Frota disponível", locadora.listarDisponiveis(categoria));
    }
}
