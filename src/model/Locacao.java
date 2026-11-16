package model;

import exception.LocadoraException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Locacao implements Identificavel {
    private static final long serialVersionUID = 1L;

    public static final double TAXA_SEGURO = 25.0;
    public static final double FATOR_MULTA = 1.5;

    private static int proximoId = 1;

    private final int id;
    private final Cliente cliente;
    private final Veiculo veiculo;
    private final Funcionario funcionario;
    private final LocalDate dataRetirada;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private final int kmRetirada;
    private int kmDevolucao;
    private final boolean comSeguro;
    private final double valorPrevisto;
    private double valorFinal;
    private SituacaoLocacao situacao;

    // TODO: cria a locacao ativa e calcula o valor previsto RN05
    public Locacao(Cliente cliente, Veiculo veiculo, Funcionario funcionario, LocalDate dataRetirada,
                   LocalDate dataPrevistaDevolucao, boolean comSeguro) throws LocadoraException {
        if (!dataPrevistaDevolucao.isAfter(dataRetirada))
            throw new LocadoraException("A devolução prevista deve ser depois da retirada.");

        this.cliente = cliente;
        this.veiculo = veiculo;
        this.funcionario = funcionario;
        this.dataRetirada = dataRetirada;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.kmRetirada = veiculo.getQuilometragem();
        this.comSeguro = comSeguro;
        this.valorPrevisto = calcularValorPrevisto(veiculo.calcularValorDiaria(),
                diariasEntre(dataRetirada, dataPrevistaDevolucao), comSeguro);
        this.situacao = SituacaoLocacao.ATIVA;
        this.id = proximoId++;
    }

    // TODO: conta as diarias entre duas datas no minimo uma RN05
    public static long diariasEntre(LocalDate inicio, LocalDate fim) {
        return Math.max(1, ChronoUnit.DAYS.between(inicio, fim));
    }

    // TODO: desconto de 5% 7 a 14 10% 15 a 29 e 15% 30 ou mais RN06
    public static double descontoPorDiarias(long diarias) {
        if (diarias >= 30)
            return 0.15;
        if (diarias >= 15)
            return 0.10;
        if (diarias >= 7)
            return 0.05;
        return 0;
    }

    // TODO: diarias x valor x 1 desconto mais seguro RN06 RN07
    public static double calcularValorPrevisto(double valorDiaria, long diarias, boolean comSeguro) {
        double valor = diarias * valorDiaria * (1 - descontoPorDiarias(diarias));
        if (comSeguro)
            valor += TAXA_SEGURO * diarias;
        return Formato.centavos(valor);
    }

    // TODO: conta as diarias da retirada ate a data
    public long calcularDiarias(LocalDate ate) {
        return diariasEntre(dataRetirada, ate);
    }

    // TODO: devolve o desconto para o numero de diarias
    public double calcularDesconto(long diarias) {
        return descontoPorDiarias(diarias);
    }

    // TODO: calcula o valor para o numero de diarias com o seguro se contratado
    public double calcularValor(long diarias) {
        return calcularValorPrevisto(veiculo.calcularValorDiaria(), diarias, comSeguro);
    }

    // TODO: conta os dias apos a data prevista zero se nao houver atraso
    public long calcularDiasAtraso(LocalDate ate) {
        return Math.max(0, ChronoUnit.DAYS.between(dataPrevistaDevolucao, ate));
    }

    // TODO: multa de 1,5 diaria por dia de atraso RN08
    public double calcularMulta(LocalDate devolucao) {
        return Formato.centavos(FATOR_MULTA * veiculo.calcularValorDiaria() * calcularDiasAtraso(devolucao));
    }

    /**
     * Encerra a locacao O valor e recalculado pelas diarias realmente usadas limitadas as
     * previstas dias alem do prazo nao sao cobrados como diaria so pela multa de atraso
     */
    // TODO: encerra a locacao cobra as diarias usadas mais a multa e libera o veiculo
    public void finalizar(LocalDate devolucao, int km) throws LocadoraException {
        if (situacao != SituacaoLocacao.ATIVA)
            throw new LocadoraException("A locação " + id + " não está ativa (situação: " + situacao + ").");
        if (devolucao.isBefore(dataRetirada))
            throw new LocadoraException("A devolução não pode ser antes da retirada.");
        if (km < kmRetirada)
            throw new LocadoraException("A quilometragem de devolução (" + km
                    + ") é menor que a da retirada (" + kmRetirada + ").");

        long diariasUsadas = Math.min(calcularDiarias(devolucao), calcularDiarias(dataPrevistaDevolucao));
        this.valorFinal = Formato.centavos(calcularValor(diariasUsadas) + calcularMulta(devolucao));
        this.dataDevolucao = devolucao;
        this.kmDevolucao = km;
        this.situacao = SituacaoLocacao.FINALIZADA;
        veiculo.setQuilometragem(km);
        veiculo.liberar();
    }

    // TODO: cancela a locacao ativa sem cobranca RN09
    public void cancelar() throws LocadoraException {
        if (situacao != SituacaoLocacao.ATIVA)
            throw new LocadoraException("Só é possível cancelar locação ativa (situação atual: " + situacao + ").");
        this.valorFinal = 0;
        this.situacao = SituacaoLocacao.CANCELADA;
        veiculo.liberar();
    }

    // TODO: indica se esta ativa e passou da data prevista
    public boolean isAtrasada(LocalDate hoje) {
        return situacao == SituacaoLocacao.ATIVA && hoje.isAfter(dataPrevistaDevolucao);
    }

    // TODO: reajusta o contador de codigos depois de carregar o arquivo
    public static void atualizarContador(int maiorId) {
        if (maiorId >= proximoId)
            proximoId = maiorId + 1;
    }

    // TODO: devolve id
    @Override
    public int getId() {
        return id;
    }

    // TODO: devolve cliente
    public Cliente getCliente() {
        return cliente;
    }

    // TODO: devolve veiculo
    public Veiculo getVeiculo() {
        return veiculo;
    }

    // TODO: devolve funcionario
    public Funcionario getFuncionario() {
        return funcionario;
    }

    // TODO: devolve dataRetirada
    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    // TODO: devolve dataPrevistaDevolucao
    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    // TODO: devolve dataDevolucao
    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    // TODO: devolve kmRetirada
    public int getKmRetirada() {
        return kmRetirada;
    }

    // TODO: devolve kmDevolucao
    public int getKmDevolucao() {
        return kmDevolucao;
    }

    // TODO: devolve comSeguro
    public boolean isComSeguro() {
        return comSeguro;
    }

    // TODO: devolve valorPrevisto
    public double getValorPrevisto() {
        return valorPrevisto;
    }

    // TODO: devolve valorFinal
    public double getValorFinal() {
        return valorFinal;
    }

    // TODO: devolve situacao
    public SituacaoLocacao getSituacao() {
        return situacao;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        String valor = situacao == SituacaoLocacao.ATIVA
                ? "Previsto " + Formato.moeda(valorPrevisto)
                : "Final " + Formato.moeda(valorFinal);
        return "[" + id + "] " + situacao + " | Cliente " + cliente.getNome()
                + " | Veículo " + veiculo.getPlaca() + " (" + veiculo.getModelo() + ")"
                + " | Retirada " + Formato.data(dataRetirada)
                + " | Prevista " + Formato.data(dataPrevistaDevolucao)
                + (dataDevolucao == null ? "" : " | Devolvida " + Formato.data(dataDevolucao))
                + " | Seguro " + (comSeguro ? "sim" : "não")
                + " | " + valor + " | Atendente " + funcionario.getNome();
    }
}
