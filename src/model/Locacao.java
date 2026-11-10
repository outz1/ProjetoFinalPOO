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

    public Locacao(Cliente cliente, Veiculo veiculo, Funcionario funcionario, LocalDate dataRetirada,
                   LocalDate dataPrevistaDevolucao, boolean comSeguro) throws LocadoraException {
        // RN05: a devolução prevista deve ser depois da retirada
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

    /** Número de diárias entre duas datas; o mínimo é uma diária (RN05). */
    public static long diariasEntre(LocalDate inicio, LocalDate fim) {
        return Math.max(1, ChronoUnit.DAYS.between(inicio, fim));
    }

    /** RN06: 5% de 7 a 14 diárias, 10% de 15 a 29 e 15% a partir de 30. */
    public static double descontoPorDiarias(long diarias) {
        if (diarias >= 30)
            return 0.15;
        if (diarias >= 15)
            return 0.10;
        if (diarias >= 7)
            return 0.05;
        return 0;
    }

    /** Vprevisto = n * d * (1 - desconto) + 25 * n * seguro (RN06 e RN07). */
    public static double calcularValorPrevisto(double valorDiaria, long diarias, boolean comSeguro) {
        double valor = diarias * valorDiaria * (1 - descontoPorDiarias(diarias));
        if (comSeguro)
            valor += TAXA_SEGURO * diarias;
        return Formato.centavos(valor);
    }

    public long calcularDiarias(LocalDate ate) {
        return diariasEntre(dataRetirada, ate);
    }

    public double calcularDesconto(long diarias) {
        return descontoPorDiarias(diarias);
    }

    public double calcularValor(long diarias) {
        return calcularValorPrevisto(veiculo.calcularValorDiaria(), diarias, comSeguro);
    }

    public long calcularDiasAtraso(LocalDate ate) {
        return Math.max(0, ChronoUnit.DAYS.between(dataPrevistaDevolucao, ate));
    }

    /** RN08: cada dia de atraso custa 1,5 diária. */
    public double calcularMulta(LocalDate devolucao) {
        return Formato.centavos(FATOR_MULTA * veiculo.calcularValorDiaria() * calcularDiasAtraso(devolucao));
    }

    /**
     * Encerra a locação. O valor é recalculado pelas diárias realmente usadas, limitadas às
     * previstas: dias além do prazo não são cobrados como diária, só pela multa de atraso.
     */
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

    /** RN09: locação cancelada não é cobrada. */
    public void cancelar() throws LocadoraException {
        if (situacao != SituacaoLocacao.ATIVA)
            throw new LocadoraException("Só é possível cancelar locação ativa (situação atual: " + situacao + ").");
        this.valorFinal = 0;
        this.situacao = SituacaoLocacao.CANCELADA;
        veiculo.liberar();
    }

    public boolean isAtrasada(LocalDate hoje) {
        return situacao == SituacaoLocacao.ATIVA && hoje.isAfter(dataPrevistaDevolucao);
    }

    /** Chamado depois de carregar o arquivo, para que os próximos códigos não se repitam. */
    public static void atualizarContador(int maiorId) {
        if (maiorId >= proximoId)
            proximoId = maiorId + 1;
    }

    @Override
    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public int getKmRetirada() {
        return kmRetirada;
    }

    public int getKmDevolucao() {
        return kmDevolucao;
    }

    public boolean isComSeguro() {
        return comSeguro;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public SituacaoLocacao getSituacao() {
        return situacao;
    }

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
