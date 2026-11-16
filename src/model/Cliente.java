package model;

import java.time.LocalDate;

public class Cliente extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String numeroCnh;
    private String categoriaCnh;
    private LocalDate validadeCnh;
    private String endereco;

    // TODO: cria o cliente com os dados da CNH e o endereco
    public Cliente(String nome, String cpf, String telefone, String email, LocalDate dataNascimento,
                   String numeroCnh, String categoriaCnh, LocalDate validadeCnh, String endereco) {
        super(nome, cpf, telefone, email, dataNascimento);
        this.numeroCnh = numeroCnh;
        this.categoriaCnh = categoriaCnh;
        this.validadeCnh = validadeCnh;
        this.endereco = endereco;
    }

    // TODO: indica se a CNH vale na data ate o dia da validade inclusive
    public boolean isCnhValida(LocalDate data) {
        return !validadeCnh.isBefore(data);
    }

    // TODO: indica se a CNH tem a categoria AB tem A e B
    public boolean possuiCategoria(String categoria) {
        return categoriaCnh.toUpperCase().contains(categoria.trim().toUpperCase());
    }

    // TODO: devolve Cliente
    @Override
    public String getTipo() {
        return "Cliente";
    }

    // TODO: devolve numeroCnh
    public String getNumeroCnh() {
        return numeroCnh;
    }

    // TODO: define numeroCnh
    public void setNumeroCnh(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    // TODO: devolve categoriaCnh
    public String getCategoriaCnh() {
        return categoriaCnh;
    }

    // TODO: define categoriaCnh
    public void setCategoriaCnh(String categoriaCnh) {
        this.categoriaCnh = categoriaCnh;
    }

    // TODO: devolve validadeCnh
    public LocalDate getValidadeCnh() {
        return validadeCnh;
    }

    // TODO: define validadeCnh
    public void setValidadeCnh(LocalDate validadeCnh) {
        this.validadeCnh = validadeCnh;
    }

    // TODO: devolve endereco
    public String getEndereco() {
        return endereco;
    }

    // TODO: define endereco
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return super.toString() + " | CNH " + numeroCnh + " cat. " + categoriaCnh
                + " (validade " + Formato.data(validadeCnh) + ") | Endereço " + endereco;
    }
}
