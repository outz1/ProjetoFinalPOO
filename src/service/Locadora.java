package service;

import exception.HabilitacaoInvalidaException;
import exception.LocacaoAtivaExistenteException;
import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import exception.VeiculoIndisponivelException;
import model.Cliente;
import model.Formato;
import model.Funcionario;
import model.Identificavel;
import model.Locacao;
import model.SituacaoVeiculo;
import model.Veiculo;
import persistence.ArquivoDados;
import repository.ClienteRepositorio;
import repository.FuncionarioRepositorio;
import repository.LocacaoRepositorio;
import repository.RepositorioBase;
import repository.VeiculoRepositorio;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras que envolvem várias entidades. Toda operação que altera dados termina em salvarDados()
 * (RF13), então a interface só precisa chamar os métodos daqui.
 */
public class Locadora {
    public static final int IDADE_MINIMA = 21;

    private final VeiculoRepositorio veiculos = new VeiculoRepositorio();
    private final ClienteRepositorio clientes = new ClienteRepositorio();
    private final FuncionarioRepositorio funcionarios = new FuncionarioRepositorio();
    private final LocacaoRepositorio locacoes = new LocacaoRepositorio();
    private final ArquivoDados arquivo;

    public Locadora() {
        this(new ArquivoDados());
    }

    public Locadora(ArquivoDados arquivo) {
        this.arquivo = arquivo;
    }

    public VeiculoRepositorio getVeiculos() {
        return veiculos;
    }

    public ClienteRepositorio getClientes() {
        return clientes;
    }

    public FuncionarioRepositorio getFuncionarios() {
        return funcionarios;
    }

    public LocacaoRepositorio getLocacoes() {
        return locacoes;
    }

    // ---- Veículos (UC01)

    public void incluirVeiculo(Veiculo veiculo) throws LocadoraException {
        veiculos.inserir(veiculo);
        salvarDados();
    }

    /** O veículo já foi mudado pela tela; aqui só se confere a placa e se grava. */
    public void alterarVeiculo(Veiculo veiculo) throws LocadoraException {
        alterar(veiculos, veiculo);
    }

    /** RN09: veículo com locação ativa não pode ser excluído. */
    public void excluirVeiculo(int id) throws LocadoraException {
        Veiculo veiculo = veiculos.buscarPorId(id);
        if (locacoes.buscarAtivaPorVeiculo(id) != null)
            throw new LocadoraException("O veículo " + veiculo.getPlaca() + " tem locação ativa e não pode ser excluído.");
        veiculos.remover(id);
        salvarDados();
    }

    public void enviarParaManutencao(int id) throws LocadoraException {
        veiculos.buscarPorId(id).enviarParaManutencao();
        salvarDados();
    }

    public void retornarDaManutencao(int id) throws LocadoraException {
        Veiculo veiculo = veiculos.buscarPorId(id);
        if (veiculo.getSituacao() != SituacaoVeiculo.MANUTENCAO)
            throw new LocadoraException("O veículo " + veiculo.getPlaca() + " não está em manutenção.");
        veiculo.liberar();
        salvarDados();
    }

    // ---- Clientes (UC02)

    public void incluirCliente(Cliente cliente) throws LocadoraException {
        clientes.inserir(cliente);
        salvarDados();
    }

    public void alterarCliente(Cliente cliente) throws LocadoraException {
        alterar(clientes, cliente);
    }

    /** RN09: cliente com locação ativa não pode ser excluído. */
    public void excluirCliente(int id) throws LocadoraException {
        Cliente cliente = clientes.buscarPorId(id);
        if (locacoes.buscarAtivaPorCliente(id) != null)
            throw new LocadoraException("O cliente " + cliente.getNome() + " tem locação ativa e não pode ser excluído.");
        clientes.remover(id);
        salvarDados();
    }

    // ---- Funcionários (UC03)

    public void incluirFuncionario(Funcionario funcionario) throws LocadoraException {
        funcionarios.inserir(funcionario);
        salvarDados();
    }

    public void alterarFuncionario(Funcionario funcionario) throws LocadoraException {
        alterar(funcionarios, funcionario);
    }

    public void excluirFuncionario(int id) throws LocadoraException {
        funcionarios.remover(id);
        salvarDados();
    }

    // ---- Locações (UC04 a UC09)

    /** UC05 e UC06 sem gravar nada: confere as regras e devolve o valor previsto para o atendente confirmar. */
    public double simularLocacao(int idCli, int idVei, LocalDate retirada, LocalDate prevista, boolean comSeguro)
            throws LocadoraException {
        Cliente cliente = clientes.buscarPorId(idCli);
        Veiculo veiculo = veiculos.buscarPorId(idVei);
        validarLocacao(cliente, veiculo, retirada, prevista);
        return Locacao.calcularValorPrevisto(veiculo.calcularValorDiaria(),
                Locacao.diariasEntre(retirada, prevista), comSeguro);
    }

    /** UC04: se qualquer regra falhar, nada é gravado e o veículo continua como estava. */
    public Locacao realizarLocacao(int idCli, int idVei, int idFunc, LocalDate retirada, LocalDate prevista,
                                   boolean comSeguro) throws LocadoraException {
        Cliente cliente = clientes.buscarPorId(idCli);
        Veiculo veiculo = veiculos.buscarPorId(idVei);
        Funcionario funcionario = funcionarios.buscarPorId(idFunc);
        validarLocacao(cliente, veiculo, retirada, prevista);

        Locacao locacao = new Locacao(cliente, veiculo, funcionario, retirada, prevista, comSeguro);
        locacoes.inserir(locacao);
        veiculo.locar();
        salvarDados();
        return locacao;
    }

    /** UC07: devolve o valor final (diárias usadas mais multa por atraso, se houver). */
    public double registrarDevolucao(int idLocacao, LocalDate data, int km) throws LocadoraException {
        Locacao locacao = locacoes.buscarPorId(idLocacao);
        locacao.finalizar(data, km);
        salvarDados();
        return locacao.getValorFinal();
    }

    /** UC09: cancela sem cobrança e libera o veículo. */
    public void cancelarLocacao(int idLocacao) throws LocadoraException {
        locacoes.buscarPorId(idLocacao).cancelar();
        salvarDados();
    }

    /** Aceita o código da locação ou a placa do veículo (UC07, passo 1). */
    public Locacao localizarLocacaoAtiva(String codigoOuPlaca) throws RegistroNaoEncontradoException {
        String texto = codigoOuPlaca.trim();
        if (texto.matches("\\d{1,9}"))
            return locacoes.buscarPorId(Integer.parseInt(texto));
        Veiculo veiculo = veiculos.buscarPorPlaca(texto);
        Locacao locacao = locacoes.buscarAtivaPorVeiculo(veiculo.getId());
        if (locacao == null)
            throw new RegistroNaoEncontradoException("locação ativa", "placa " + veiculo.getPlaca());
        return locacao;
    }

    // ---- Consultas (UC10 e UC11)

    /** Categoria em branco ou nula lista todas. */
    public List<Veiculo> listarDisponiveis(String categoria) {
        List<Veiculo> disponiveis = veiculos.listarPorSituacao(SituacaoVeiculo.DISPONIVEL);
        if (categoria == null || categoria.isBlank())
            return disponiveis;
        return disponiveis.stream().filter(v -> v.getCategoria().equalsIgnoreCase(categoria.trim())).toList();
    }

    public List<Locacao> historicoPorCliente(String cpf) throws RegistroNaoEncontradoException {
        return locacoes.listarPorCliente(clientes.buscarPorCpf(cpf).getId());
    }

    public List<Locacao> historicoPorVeiculo(String placa) throws RegistroNaoEncontradoException {
        return locacoes.listarPorVeiculo(veiculos.buscarPorPlaca(placa).getId());
    }

    public List<Locacao> listarAtrasadas(LocalDate hoje) {
        return locacoes.listarAtivas().stream().filter(l -> l.isAtrasada(hoje)).toList();
    }

    // ---- Regras de locação

    /** RN01 a RN05, na ordem em que o atendente costuma resolver cada problema. */
    private void validarLocacao(Cliente cliente, Veiculo veiculo, LocalDate retirada, LocalDate prevista)
            throws LocadoraException {
        if (retirada == null || prevista == null)
            throw new LocadoraException("Informe as datas de retirada e de devolução.");
        if (!prevista.isAfter(retirada))
            throw new LocadoraException("A devolução prevista deve ser depois da retirada.");
        if (!veiculo.isDisponivel())
            throw new VeiculoIndisponivelException(veiculo.getPlaca(), veiculo.getSituacao().toString());
        validarHabilitacao(cliente, veiculo, retirada);
        if (locacoes.buscarAtivaPorCliente(cliente.getId()) != null)
            throw new LocacaoAtivaExistenteException(cliente.getNome());
    }

    /** RN02 e RN03, conferidas na data da retirada. */
    private void validarHabilitacao(Cliente cliente, Veiculo veiculo, LocalDate data) throws HabilitacaoInvalidaException {
        int idade = cliente.calcularIdade(data);
        if (idade < IDADE_MINIMA)
            throw new HabilitacaoInvalidaException("O cliente precisa ter " + IDADE_MINIMA
                    + " anos ou mais (idade na retirada: " + idade + ").");
        if (!cliente.isCnhValida(data))
            throw new HabilitacaoInvalidaException("A CNH do cliente venceu em " + Formato.data(cliente.getValidadeCnh()) + ".");
        String exigida = veiculo.getCnhExigida();
        if (!cliente.possuiCategoria(exigida))
            throw new HabilitacaoInvalidaException("CNH incompatível: " + veiculo.getCategoria() + " exige categoria "
                    + exigida + " e o cliente possui " + cliente.getCategoriaCnh() + ".");
    }

    // ---- Persistência (RF13)

    /**
     * Como a tela muda o objeto antes de pedir a alteração, uma placa ou CPF repetido já foi aplicado em
     * memória quando o repositório recusa. Nesse caso volta-se ao que está no arquivo, que tem todas as
     * alterações anteriores.
     */
    private <T extends Identificavel> void alterar(RepositorioBase<T> repositorio, T item) throws LocadoraException {
        try {
            repositorio.alterar(item);
        } catch (LocadoraException e) {
            carregarDados();
            throw e;
        }
        salvarDados();
    }

    public void salvarDados() throws LocadoraException {
        try {
            arquivo.salvar(this);
        } catch (IOException e) {
            throw new LocadoraException("Não foi possível salvar os dados em " + arquivo.getCaminho() + ": " + e.getMessage(), e);
        }
    }

    public void carregarDados() throws LocadoraException {
        try {
            arquivo.carregar(this);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new LocadoraException("Não foi possível ler os dados de " + arquivo.getCaminho() + ": " + e.getMessage(), e);
        }
    }
}
