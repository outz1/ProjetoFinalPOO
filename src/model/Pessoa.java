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

    // TODO: criaa a pessoa com novo codigo
    public Pessoa(String nome, String cpf, String telefone, String email, LocalDate dataNascimento) {
        this.id = proximoId++;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    // TODO: calcula a idade em anos na data informada
    public int calcularIdade(LocalDate data) {
        return Period.between(dataNascimento, data).getYears();
    }

    // TODO: devolve o tipo da pessoa
    public abstract String getTipo();

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

    // TODO: devolve nome
    public String getNome() {
        return nome;
    }

    // TODO: define nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    // TODO: devolve cpf
    public String getCpf() {
        return cpf;
    }

    // TODO: define cpf
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    // TODO: devolve telefone
    public String getTelefone() {
        return telefone;
    }

    // TODO: define telefone
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // TODO: devolve email
    public String getEmail() {
        return email;
    }

    // TODO: define email
    public void setEmail(String email) {
        this.email = email;
    }

    // TODO: devolve dataNascimento
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    // TODO: define dataNascimento
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | CPF " + cpf + " | Nascimento " + Formato.data(dataNascimento)
                + " | Telefone " + telefone + " | E-mail " + email;
    }
}
