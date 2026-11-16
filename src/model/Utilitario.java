package model;

public class Utilitario extends Veiculo {
    private static final long serialVersionUID = 1L;

    public static final String CATEGORIA = "Utilitário";
    private static final double ADICIONAL_POR_KG = 0.02;
    private static final double LIMITE_CARGA_CATEGORIA_B = 1500;

    private double capacidadeCargaKg;
    private String tipoCarroceria;

    // TODO: cria o utilitario com carga e carroceria
    public Utilitario(String placa, String marca, String modelo, int ano, String cor, int quilometragem,
                      double valorDiariaBase, double capacidadeCargaKg, String tipoCarroceria) {
        super(placa, marca, modelo, ano, cor, quilometragem, valorDiariaBase);
        this.capacidadeCargaKg = capacidadeCargaKg;
        this.tipoCarroceria = tipoCarroceria;
    }

    // TODO: diaria base mais R$ 0,02 por kg de carga
    @Override
    public double calcularValorDiaria() {
        return Formato.centavos(valorDiariaBase + ADICIONAL_POR_KG * capacidadeCargaKg);
    }

    // TODO: exige B ate 1,500 kg de carga e C acima disso
    @Override
    public String getCnhExigida() {
        return capacidadeCargaKg <= LIMITE_CARGA_CATEGORIA_B ? "B" : "C";
    }

    // TODO: devolve a categoria do veiculo
    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    // TODO: devolve capacidadeCargaKg
    public double getCapacidadeCargaKg() {
        return capacidadeCargaKg;
    }

    // TODO: define capacidadeCargaKg
    public void setCapacidadeCargaKg(double capacidadeCargaKg) {
        this.capacidadeCargaKg = capacidadeCargaKg;
    }

    // TODO: devolve tipoCarroceria
    public String getTipoCarroceria() {
        return tipoCarroceria;
    }

    // TODO: define tipoCarroceria
    public void setTipoCarroceria(String tipoCarroceria) {
        this.tipoCarroceria = tipoCarroceria;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return super.toString() + " | Carga " + Formato.decimal(capacidadeCargaKg) + " kg | Carroceria " + tipoCarroceria;
    }
}
