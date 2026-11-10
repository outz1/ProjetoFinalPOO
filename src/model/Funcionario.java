package model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String matricula;
    private Cargo cargo;

    public Funcionario(String nome, String cpf, String telefone, String email, LocalDate dataNascimento,
                       String matricula, Cargo cargo) {
        super(nome, cpf, telefone, email, dataNascimento);
        this.matricula = matricula;
        this.cargo = cargo;
    }

    @Override
    public String getTipo() {
        return "Funcionário";
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return super.toString() + " | Matrícula " + matricula + " | Cargo " + cargo;
    }
}
