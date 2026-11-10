package model;

import exception.LocadoraException;
import exception.VeiculoIndisponivelException;
import java.util.List;

public abstract class Veiculo implements Identificavel, Tarifavel {
    private static final long serialVersionUID = 1L;

    public static final List<String> CATEGORIAS = List.of(Carro.CATEGORIA, Moto.CATEGORIA, Utilitario.CATEGORIA);

    private static int proximoId = 1;

    private final int id;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;
    private int quilometragem;
    protected double valorDiariaBase;
    private SituacaoVeiculo situacao;

    public Veiculo(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                   double valorDiariaBase) {
        this.id = proximoId++;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.valorDiariaBase = valorDiariaBase;
        this.situacao = SituacaoVeiculo.DISPONIVEL;
    }

    @Override
    public abstract double calcularValorDiaria();

    public abstract String getCnhExigida();

    public abstract String getCategoria();

    public boolean isDisponivel() {
        return situacao == SituacaoVeiculo.DISPONIVEL;
    }

    /** RN01: só o veículo Disponível pode ser locado. */
    public void locar() throws VeiculoIndisponivelException {
        if (!isDisponivel())
            throw new VeiculoIndisponivelException(placa, situacao.toString());
        situacao = SituacaoVeiculo.LOCADO;
    }

    /** Volta para a frota (devolução, cancelamento ou fim da manutenção). */
    public void liberar() {
        situacao = SituacaoVeiculo.DISPONIVEL;
    }

    public void enviarParaManutencao() throws LocadoraException {
        if (situacao == SituacaoVeiculo.LOCADO)
            throw new LocadoraException("O veículo " + placa + " está locado e não pode ir para manutenção.");
        situacao = SituacaoVeiculo.MANUTENCAO;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public int getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }

    public double getValorDiariaBase() {
        return valorDiariaBase;
    }

    public void setValorDiariaBase(double valorDiariaBase) {
        this.valorDiariaBase = valorDiariaBase;
    }

    public SituacaoVeiculo getSituacao() {
        return situacao;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + getCategoria() + " " + placa + " | " + marca + " " + modelo + " " + ano
                + " | Cor " + cor + " | " + quilometragem + " km"
                + " | Diária " + Formato.moeda(calcularValorDiaria())
                + " | CNH " + getCnhExigida() + " | " + situacao;
    }
}
