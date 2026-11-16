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

    // TODO: cria o veiculo Disponivel e com novo codigo
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

    // TODO: calcula a diaria conforme a categoria
    @Override
    public abstract double calcularValorDiaria();

    // TODO: devolve a categoria de CNH exigida
    public abstract String getCnhExigida();

    // TODO: devolve a categoria do veiculo
    public abstract String getCategoria();

    // TODO: indica se o veiculo esta Disponivel
    public boolean isDisponivel() {
        return situacao == SituacaoVeiculo.DISPONIVEL;
    }

    // TODO: marca como Locado so o Disponivel pode ser locado RN01
    public void locar() throws VeiculoIndisponivelException {
        if (!isDisponivel())
            throw new VeiculoIndisponivelException(placa, situacao.toString());
        situacao = SituacaoVeiculo.LOCADO;
    }

    // TODO: volta para a frota devolucao cancelamento ou fim da manutencao
    public void liberar() {
        situacao = SituacaoVeiculo.DISPONIVEL;
    }

    // TODO: marca como Em manutencao exceto se estiver locado
    public void enviarParaManutencao() throws LocadoraException {
        if (situacao == SituacaoVeiculo.LOCADO)
            throw new LocadoraException("O veículo " + placa + " está locado e não pode ir para manutenção.");
        situacao = SituacaoVeiculo.MANUTENCAO;
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

    // TODO: devolve placa
    public String getPlaca() {
        return placa;
    }

    // TODO: define placa
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    // TODO: devolve marca
    public String getMarca() {
        return marca;
    }

    // TODO: define marca
    public void setMarca(String marca) {
        this.marca = marca;
    }

    // TODO: devolve modelo
    public String getModelo() {
        return modelo;
    }

    // TODO: define modelo
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    // TODO: devolve ano
    public int getAno() {
        return ano;
    }

    // TODO: define ano
    public void setAno(int ano) {
        this.ano = ano;
    }

    // TODO: devolve cor
    public String getCor() {
        return cor;
    }

    // TODO: define cor
    public void setCor(String cor) {
        this.cor = cor;
    }

    // TODO: devolve quilometragem
    public int getQuilometragem() {
        return quilometragem;
    }

    // TODO: define quilometragem
    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }

    // TODO: devolve valorDiariaBase
    public double getValorDiariaBase() {
        return valorDiariaBase;
    }

    // TODO: define valorDiariaBase
    public void setValorDiariaBase(double valorDiariaBase) {
        this.valorDiariaBase = valorDiariaBase;
    }

    // TODO: devolve situacao
    public SituacaoVeiculo getSituacao() {
        return situacao;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return "[" + id + "] " + getCategoria() + " " + placa + " | " + marca + " " + modelo + " " + ano
                + " | Cor " + cor + " | " + quilometragem + " km"
                + " | Diária " + Formato.moeda(calcularValorDiaria())
                + " | CNH " + getCnhExigida() + " | " + situacao;
    }
}
