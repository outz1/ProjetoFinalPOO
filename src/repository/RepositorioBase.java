package repository;

import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import model.Identificavel;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositorioBase<T extends Identificavel> implements Repositorio<T> {
    protected ArrayList<T> itens = new ArrayList<>();
    private final String entidade;

    protected RepositorioBase(String entidade) {
        this.entidade = entidade;
    }

    @Override
    public void inserir(T item) throws LocadoraException {
        validarInclusao(item);
        itens.add(item);
    }

    @Override
    public void remover(int id) throws RegistroNaoEncontradoException {
        itens.remove(buscarPorId(id));
    }

    /** Substitui o registro de mesmo código, depois de conferir as chaves únicas. */
    @Override
    public void alterar(T item) throws LocadoraException {
        int indice = itens.indexOf(buscarPorId(item.getId()));
        validarInclusao(item);
        itens.set(indice, item);
    }

    @Override
    public T buscarPorId(int id) throws RegistroNaoEncontradoException {
        for (T item : itens) {
            if (item.getId() == id)
                return item;
        }
        throw new RegistroNaoEncontradoException(entidade, "código " + id);
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(itens);
    }

    public ArrayList<T> getItens() {
        return itens;
    }

    public void setItens(ArrayList<T> itens) {
        this.itens = itens;
    }

    /** Confere as chaves únicas contra os outros registros (o próprio código é ignorado). */
    protected abstract void validarInclusao(T item) throws LocadoraException;
}
