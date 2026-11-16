package model;

public class Moto extends Veiculo {
    private static final long serialVersionUID = 1L;

    public static final String CATEGORIA = "Moto";
    private static final int LIMITE_CILINDRADAS = 500;
    private static final double ACRESCIMO_POTENTE = 0.20;

    private int cilindradas;

    // TODO: cria a moto com cilindradas
    public Moto(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                double valorDiariaBase, int cilindradas) {
        super(placa, marca, modelo, ano, cor, quilometragem, valorDiariaBase);
        this.cilindradas = cilindradas;
    }

    // TODO: diaria base com 20% a mais acima de 500 cc
    @Override
    public double calcularValorDiaria() {
        return Formato.centavos(cilindradas > LIMITE_CILINDRADAS ? valorDiariaBase * (1 + ACRESCIMO_POTENTE) : valorDiariaBase);
    }

    // TODO: exige CNH A
    @Override
    public String getCnhExigida() {
        return "A";
    }

    // TODO: devolve a categoria do veiculo
    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    // TODO: devolve cilindradas
    public int getCilindradas() {
        return cilindradas;
    }

    // TODO: define cilindradas
    public void setCilindradas(int cilindradas) {
        this.cilindradas = cilindradas;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return super.toString() + " | " + cilindradas + " cc";
    }
}
