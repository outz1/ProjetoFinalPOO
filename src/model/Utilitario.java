package model;

public class Utilitario extends Veiculo {
    private static final long serialVersionUID = 1L;

    public static final String CATEGORIA = "Utilitário";
    private static final double ADICIONAL_POR_KG = 0.02;
    private static final double LIMITE_CARGA_CATEGORIA_B = 1500;

    private double capacidadeCargaKg;
    private String tipoCarroceria;

    public Utilitario(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                      double valorDiariaBase, double capacidadeCargaKg, String tipoCarroceria) {
        super(placa, marca, modelo, ano, cor, quilometragem, valorDiariaBase);
        this.capacidadeCargaKg = capacidadeCargaKg;
        this.tipoCarroceria = tipoCarroceria;
    }

    /** Diária base mais R$ 0,02 por kg de capacidade de carga. */
    @Override
    public double calcularValorDiaria() {
        return Formato.centavos(valorDiariaBase + ADICIONAL_POR_KG * capacidadeCargaKg);
    }

    /** B até 1.500 kg de carga; C acima disso. */
    @Override
    public String getCnhExigida() {
        return capacidadeCargaKg <= LIMITE_CARGA_CATEGORIA_B ? "B" : "C";
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    public double getCapacidadeCargaKg() {
        return capacidadeCargaKg;
    }

    public void setCapacidadeCargaKg(double capacidadeCargaKg) {
        this.capacidadeCargaKg = capacidadeCargaKg;
    }

    public String getTipoCarroceria() {
        return tipoCarroceria;
    }

    public void setTipoCarroceria(String tipoCarroceria) {
        this.tipoCarroceria = tipoCarroceria;
    }

    @Override
    public String toString() {
        return super.toString() + " | Carga " + Formato.decimal(capacidadeCargaKg) + " kg | Carroceria " + tipoCarroceria;
    }
}
