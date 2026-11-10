package model;

public class Carro extends Veiculo {
    private static final long serialVersionUID = 1L;

    public static final String CATEGORIA = "Carro";
    private static final double ACRESCIMO_AUTOMATICO = 0.10;

    private int numeroPortas;
    private boolean automatico;
    private boolean arCondicionado;

    public Carro(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                 double valorDiariaBase, int numeroPortas, boolean automatico, boolean arCondicionado) {
        super(placa, marca, modelo, ano, cor, quilometragem, valorDiariaBase);
        this.numeroPortas = numeroPortas;
        this.automatico = automatico;
        this.arCondicionado = arCondicionado;
    }

    /** Diária base, com 10% a mais se o câmbio for automático. */
    @Override
    public double calcularValorDiaria() {
        return Formato.centavos(automatico ? valorDiariaBase * (1 + ACRESCIMO_AUTOMATICO) : valorDiariaBase);
    }

    @Override
    public String getCnhExigida() {
        return "B";
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    public int getNumeroPortas() {
        return numeroPortas;
    }

    public void setNumeroPortas(int numeroPortas) {
        this.numeroPortas = numeroPortas;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }

    public boolean isArCondicionado() {
        return arCondicionado;
    }

    public void setArCondicionado(boolean arCondicionado) {
        this.arCondicionado = arCondicionado;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + numeroPortas + " portas | Câmbio " + (automatico ? "automático" : "manual")
                + " | Ar-condicionado " + (arCondicionado ? "sim" : "não");
    }
}
