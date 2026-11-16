package model;

public class Carro extends Veiculo {
    private static final long serialVersionUID = 1L;

    public static final String CATEGORIA = "Carro";
    private static final double ACRESCIMO_AUTOMATICO = 0.10;

    private int numeroPortas;
    private boolean automatico;
    private boolean arCondicionado;

    // TODO: cria o carro com portas cambio e arcondicionado
    public Carro(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                 double valorDiariaBase, int numeroPortas, boolean automatico, boolean arCondicionado) {
        super(placa, marca, modelo, ano, cor, quilometragem, valorDiariaBase);
        this.numeroPortas = numeroPortas;
        this.automatico = automatico;
        this.arCondicionado = arCondicionado;
    }

    // TODO: diaria base com 10% a mais se o cambio for automatico
    @Override
    public double calcularValorDiaria() {
        return Formato.centavos(automatico ? valorDiariaBase * (1 + ACRESCIMO_AUTOMATICO) : valorDiariaBase);
    }

    // TODO: exige CNH B
    @Override
    public String getCnhExigida() {
        return "B";
    }

    // TODO: devolve a categoria do veiculo
    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    // TODO: devolve numeroPortas
    public int getNumeroPortas() {
        return numeroPortas;
    }

    // TODO: define numeroPortas
    public void setNumeroPortas(int numeroPortas) {
        this.numeroPortas = numeroPortas;
    }

    // TODO: devolve automatico
    public boolean isAutomatico() {
        return automatico;
    }

    // TODO: define automatico
    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }

    // TODO: devolve arCondicionado
    public boolean isArCondicionado() {
        return arCondicionado;
    }

    // TODO: define arCondicionado
    public void setArCondicionado(boolean arCondicionado) {
        this.arCondicionado = arCondicionado;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return super.toString() + " | " + numeroPortas + " portas | Câmbio " + (automatico ? "automático" : "manual")
                + " | Ar-condicionado " + (arCondicionado ? "sim" : "não");
    }
}
