package model;

import java.time.LocalDate;

public class Cliente extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String numeroCnh;
    private String categoriaCnh;
    private LocalDate validadeCnh;
    private String endereco;

    public Cliente(String nome, String cpf, String telefone, String email, LocalDate dataNascimento,
                   String numeroCnh, String categoriaCnh, LocalDate validadeCnh, String endereco) {
        super(nome, cpf, telefone, email, dataNascimento);
        this.numeroCnh = numeroCnh;
        this.categoriaCnh = categoriaCnh;
        this.validadeCnh = validadeCnh;
        this.endereco = endereco;
    }

    /** A CNH vale até o dia da validade, inclusive. */
    public boolean isCnhValida(LocalDate data) {
        return !validadeCnh.isBefore(data);
    }

    /** Uma CNH "AB" possui as categorias A e B. */
    public boolean possuiCategoria(String categoria) {
        return categoriaCnh.toUpperCase().contains(categoria.trim().toUpperCase());
    }

    @Override
    public String getTipo() {
        return "Cliente";
    }

    public String getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public String getCategoriaCnh() {
        return categoriaCnh;
    }

    public void setCategoriaCnh(String categoriaCnh) {
        this.categoriaCnh = categoriaCnh;
    }

    public LocalDate getValidadeCnh() {
        return validadeCnh;
    }

    public void setValidadeCnh(LocalDate validadeCnh) {
        this.validadeCnh = validadeCnh;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return super.toString() + " | CNH " + numeroCnh + " cat. " + categoriaCnh
                + " (validade " + Formato.data(validadeCnh) + ") | Endereço " + endereco;
    }
}
