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
 * Regras que envolvem varias entidades Toda operacao que altera dados termina em salvarDados
 * RF13 entao a interface so precisa chamar os metodos daqui
 */
public class Locadora {
    public static final int IDADE_MINIMA = 21;

    private final VeiculoRepositorio veiculos = new VeiculoRepositorio();
    private final ClienteRepositorio clientes = new ClienteRepositorio();
    private final FuncionarioRepositorio funcionarios = new FuncionarioRepositorio();
    private final LocacaoRepositorio locacoes = new LocacaoRepositorio();
    private final ArquivoDados arquivo;

    // TODO: cria a locadora com o arquivo de dados padrao ou o informado
    public Locadora() {
        this(new ArquivoDados());
    }

    // TODO: cria a locadora com o arquivo de dados padrao ou o informado
    public Locadora(ArquivoDados arquivo) {
        this.arquivo = arquivo;
    }

    // TODO: devolve veiculos
    public VeiculoRepositorio getVeiculos() {
        return veiculos;
    }

    // TODO: devolve clientes
    public ClienteRepositorio getClientes() {
        return clientes;
    }

    // TODO: devolve funcionarios
    public FuncionarioRepositorio getFuncionarios() {
        return funcionarios;
    }

    // TODO: devolve locacoes
    public LocacaoRepositorio getLocacoes() {
        return locacoes;
    }

    // TODO: inclue o veiculo e salva
    public void incluirVeiculo(Veiculo veiculo) throws LocadoraException {
        veiculos.inserir(veiculo);
        salvarDados();
    }

    // TODO: confere a placa do veiculo ja alterado pela tela e salva
    public void alterarVeiculo(Veiculo veiculo) throws LocadoraException {
        alterar(veiculos, veiculo);
    }

    // TODO: exclui o veiculo exceto se tiver locacao ativa RN09
    public void excluirVeiculo(int id) throws LocadoraException {
        Veiculo veiculo = veiculos.buscarPorId(id);
        if (locacoes.buscarAtivaPorVeiculo(id) != null)
            throw new LocadoraException("O veículo " + veiculo.getPlaca() + " tem locação ativa e não pode ser excluído.");
        veiculos.remover(id);
        salvarDados();
    }

    // TODO: envia o veiculo para manutencao e salva
    public void enviarParaManutencao(int id) throws LocadoraException {
        veiculos.buscarPorId(id).enviarParaManutencao();
        salvarDados();
    }

    // TODO: devolve o veiculo da manutencao para a frota e salva
    public void retornarDaManutencao(int id) throws LocadoraException {
        Veiculo veiculo = veiculos.buscarPorId(id);
        if (veiculo.getSituacao() != SituacaoVeiculo.MANUTENCAO)
            throw new LocadoraException("O veículo " + veiculo.getPlaca() + " não está em manutenção.");
        veiculo.liberar();
        salvarDados();
    }

    // TODO: inclui o cliente e salva
    public void incluirCliente(Cliente cliente) throws LocadoraException {
        clientes.inserir(cliente);
        salvarDados();
    }

    // TODO: confere o CPF do cliente alterado e salva
    public void alterarCliente(Cliente cliente) throws LocadoraException {
        alterar(clientes, cliente);
    }

    // TODO: exclui o cliente exceto se tiver locacao ativa RN09
    public void excluirCliente(int id) throws LocadoraException {
        Cliente cliente = clientes.buscarPorId(id);
        if (locacoes.buscarAtivaPorCliente(id) != null)
            throw new LocadoraException("O cliente " + cliente.getNome() + " tem locação ativa e não pode ser excluído.");
        clientes.remover(id);
        salvarDados();
    }

    // TODO: inclui o funcionario e salva
    public void incluirFuncionario(Funcionario funcionario) throws LocadoraException {
        funcionarios.inserir(funcionario);
        salvarDados();
    }

    // TODO: confere CPF e matricula do funcionario alterado e salva
    public void alterarFuncionario(Funcionario funcionario) throws LocadoraException {
        alterar(funcionarios, funcionario);
    }

    // TODO: exclui o funcionario e salva
    public void excluirFuncionario(int id) throws LocadoraException {
        funcionarios.remover(id);
        salvarDados();
    }

    // TODO: confere as regras sem gravar e devolve o valor previsto UC05 UC06
    public double simularLocacao(int idCli, int idVei, LocalDate retirada, LocalDate prevista, boolean comSeguro)
            throws LocadoraException {
        Cliente cliente = clientes.buscarPorId(idCli);
        Veiculo veiculo = veiculos.buscarPorId(idVei);
        validarLocacao(cliente, veiculo, retirada, prevista);
        return Locacao.calcularValorPrevisto(veiculo.calcularValorDiaria(),
                Locacao.diariasEntre(retirada, prevista), comSeguro);
    }

    // TODO: valida registra a locacao marca o veiculo como Locado e salva UC04
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

    // TODO: finaliza a locacao e devolve o valor final UC07
    public double registrarDevolucao(int idLocacao, LocalDate data, int km) throws LocadoraException {
        Locacao locacao = locacoes.buscarPorId(idLocacao);
        locacao.finalizar(data, km);
        salvarDados();
        return locacao.getValorFinal();
    }

    // TODO: cancela a locacao sem cobranca e libera o veiculo UC09
    public void cancelarLocacao(int idLocacao) throws LocadoraException {
        locacoes.buscarPorId(idLocacao).cancelar();
        salvarDados();
    }

    // TODO: acha a locacao pelo codigo ou pela placa do veiculo UC07
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

    // TODO: lista os veiculos disponiveis de uma categoria ou de todas UC10
    public List<Veiculo> listarDisponiveis(String categoria) {
        List<Veiculo> disponiveis = veiculos.listarPorSituacao(SituacaoVeiculo.DISPONIVEL);
        if (categoria == null || categoria.isBlank())
            return disponiveis;
        return disponiveis.stream().filter(v -> v.getCategoria().equalsIgnoreCase(categoria.trim())).toList();
    }

    // TODO: lista as locacoes do cliente pelo CPF UC11
    public List<Locacao> historicoPorCliente(String cpf) throws RegistroNaoEncontradoException {
        return locacoes.listarPorCliente(clientes.buscarPorCpf(cpf).getId());
    }

    // TODO: lista as locacoes do veiculo pela placa UC11
    public List<Locacao> historicoPorVeiculo(String placa) throws RegistroNaoEncontradoException {
        return locacoes.listarPorVeiculo(veiculos.buscarPorPlaca(placa).getId());
    }

    // TODO: lista as locacoes ativas que passaram da data prevista
    public List<Locacao> listarAtrasadas(LocalDate hoje) {
        return locacoes.listarAtivas().stream().filter(l -> l.isAtrasada(hoje)).toList();
    }

    // TODO: confere datas disponibilidade habilitacao e locacao ativa RN01 a RN05
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

    // TODO: confere idade validade e categoria da CNH na retirada RN02 RN03
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

    /**
     * Como a tela muda o objeto antes de pedir a alteracao uma placa ou CPF repetido ja foi aplicado em
     * memoria quando o repositorio recusa Nesse caso voltase ao que esta no arquivo que tem todas as
     * alteracoes anteriores
     */
    // TODO: persiste a alteracao ou recarrega o arquivo se o repositorio a recusar
    private <T extends Identificavel> void alterar(RepositorioBase<T> repositorio, T item) throws LocadoraException {
        try {
            repositorio.alterar(item);
        } catch (LocadoraException e) {
            carregarDados();
            throw e;
        }
        salvarDados();
    }

    // TODO: grava os dados no arquivo RF13
    public void salvarDados() throws LocadoraException {
        try {
            arquivo.salvar(this);
        } catch (IOException e) {
            throw new LocadoraException("Não foi possível salvar os dados em " + arquivo.getCaminho() + ": " + e.getMessage(), e);
        }
    }

    // TODO: le os dados do arquivo
    public void carregarDados() throws LocadoraException {
        try {
            arquivo.carregar(this);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new LocadoraException("Não foi possível ler os dados de " + arquivo.getCaminho() + ": " + e.getMessage(), e);
        }
    }
}
