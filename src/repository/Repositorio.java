package repository;

import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import model.Identificavel;
import java.util.List;

/** As operacoes de inclusao exclusao alteracao consulta e lista */
public interface Repositorio<T extends Identificavel> {
    // TODO: inclui o registro
    void inserir(T item) throws LocadoraException;

    // TODO: exclui o registro pelo codigo
    void remover(int id) throws RegistroNaoEncontradoException;

    // TODO: substitui o registro de mesmo codigo
    void alterar(T item) throws LocadoraException;

    // TODO: busca o registro pelo codigo
    T buscarPorId(int id) throws RegistroNaoEncontradoException;

    // TODO: lista todos os registros
    List<T> listar();
}
