package repository;

import exception.LocadoraException;
import exception.RegistroNaoEncontradoException;
import model.Identificavel;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositorioBase<T extends Identificavel> implements Repositorio<T> {
    protected ArrayList<T> itens = new ArrayList<>();
    private final String entidade;

    // TODO: cria o repositorio para a entidade informada
    protected RepositorioBase(String entidade) {
        this.entidade = entidade;
    }

    // TODO: confere as chaves unicas e inclui o registro
    @Override
    public void inserir(T item) throws LocadoraException {
        validarInclusao(item);
        itens.add(item);
    }

    // TODO: exclui o registro pelo codigo
    @Override
    public void remover(int id) throws RegistroNaoEncontradoException {
        itens.remove(buscarPorId(id));
    }

    // TODO: substitui o registro de mesmo codigo depois de conferir as chaves unicas
    @Override
    public void alterar(T item) throws LocadoraException {
        int indice = itens.indexOf(buscarPorId(item.getId()));
        validarInclusao(item);
        itens.set(indice, item);
    }

    // TODO: busca o registro pelo codigo
    @Override
    public T buscarPorId(int id) throws RegistroNaoEncontradoException {
        for (T item : itens) {
            if (item.getId() == id)
                return item;
        }
        throw new RegistroNaoEncontradoException(entidade, "código " + id);
    }

    // TODO: devolve uma copia da lista de registros
    @Override
    public List<T> listar() {
        return new ArrayList<>(itens);
    }

    // TODO: devolve itens
    public ArrayList<T> getItens() {
        return itens;
    }

    // TODO: define itens
    public void setItens(ArrayList<T> itens) {
        this.itens = itens;
    }

    // TODO: confere as chaves unicas contra os outros registros
    protected abstract void validarInclusao(T item) throws LocadoraException;
}
