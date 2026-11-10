package model;

import java.time.LocalDate;
import java.time.Period;

public abstract class Pessoa implements Identificavel {
    private static final long serialVersionUID = 1L;

    private static int proximoId = 1;

    private final int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;

    public Pessoa(String nome, String cpf, String telefone, String email, LocalDate dataNascimento) {
        this.id = proximoId++;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public int calcularIdade(LocalDate data) {
        return Period.between(dataNascimento, data).getYears();
    }

    public abstract String getTipo();

    /** Chamado depois de carregar o arquivo, para que os próximos códigos não se repitam. */
    public static void atualizarContador(int maiorId) {
        if (maiorId >= proximoId)
            proximoId = maiorId + 1;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | CPF " + cpf + " | Nascimento " + Formato.data(dataNascimento)
                + " | Telefone " + telefone + " | E-mail " + email;
    }
}
