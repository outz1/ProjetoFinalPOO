package repository;

import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import model.Identificavel;
import java.util.List;

/** As operações de inclusão, exclusão, alteração, consulta e lista. */
public interface Repositorio<T extends Identificavel> {
    void inserir(T item) throws LocadoraException;

    void remover(int id) throws RegistroNaoEncontradoException;

    void alterar(T item) throws LocadoraException;

    T buscarPorId(int id) throws RegistroNaoEncontradoException;

    List<T> listar();
}
