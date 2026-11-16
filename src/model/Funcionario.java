package model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String matricula;
    private Cargo cargo;

    // TODO: cria o funcionario com matricula e cargo
    public Funcionario(String nome, String cpf, String telefone, String email, LocalDate dataNascimento,
                       String matricula, Cargo cargo) {
        super(nome, cpf, telefone, email, dataNascimento);
        this.matricula = matricula;
        this.cargo = cargo;
    }

    // TODO: devolve Funcionario
    @Override
    public String getTipo() {
        return "Funcionário";
    }

    // TODO: devolve matricula
    public String getMatricula() {
        return matricula;
    }

    // TODO: define matricula
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    // TODO: devolve cargo
    public Cargo getCargo() {
        return cargo;
    }

    // TODO: define cargo
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    // TODO: monta o texto de exibicao
    @Override
    public String toString() {
        return super.toString() + " | Matrícula " + matricula + " | Cargo " + cargo;
    }
}
