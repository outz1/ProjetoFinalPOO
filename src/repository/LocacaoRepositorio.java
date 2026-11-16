package repository;

import exception.LocacaoAtivaExistenteException;
import exception.LocadoraException;
import exception.VeiculoIndisponivelException;
import model.Locacao;
import model.SituacaoLocacao;
import model.SituacaoVeiculo;
import java.util.List;

public class LocacaoRepositorio extends RepositorioBase<Locacao> {
    // TODO: cria o repositorio de locacoes
    public LocacaoRepositorio() {
        super("locação");
    }

    // TODO: lista as locacoes do cliente
    public List<Locacao> listarPorCliente(int idCliente) {
        return itens.stream().filter(l -> l.getCliente().getId() == idCliente).toList();
    }

    // TODO: lista as locacoes do veiculo
    public List<Locacao> listarPorVeiculo(int idVeiculo) {
        return itens.stream().filter(l -> l.getVeiculo().getId() == idVeiculo).toList();
    }

    // TODO: lista as locacoes ativas
    public List<Locacao> listarAtivas() {
        return itens.stream().filter(l -> l.getSituacao() == SituacaoLocacao.ATIVA).toList();
    }

    // TODO: devolve a locacao ativa do cliente ou null
    public Locacao buscarAtivaPorCliente(int idCliente) {
        for (Locacao l : itens) {
            if (l.getSituacao() == SituacaoLocacao.ATIVA && l.getCliente().getId() == idCliente)
                return l;
        }
        return null;
    }

    // TODO: devolve a locacao ativa do veiculo ou null
    public Locacao buscarAtivaPorVeiculo(int idVeiculo) {
        for (Locacao l : itens) {
            if (l.getSituacao() == SituacaoLocacao.ATIVA && l.getVeiculo().getId() == idVeiculo)
                return l;
        }
        return null;
    }

    // TODO: recusa segunda locacao ativa do mesmo cliente ou veiculo RN01 RN04
    @Override
    protected void validarInclusao(Locacao locacao) throws LocadoraException {
        if (locacao.getSituacao() != SituacaoLocacao.ATIVA)
            return;
        Locacao doCliente = buscarAtivaPorCliente(locacao.getCliente().getId());
        if (doCliente != null && doCliente.getId() != locacao.getId())
            throw new LocacaoAtivaExistenteException(locacao.getCliente().getNome());
        Locacao doVeiculo = buscarAtivaPorVeiculo(locacao.getVeiculo().getId());
        if (doVeiculo != null && doVeiculo.getId() != locacao.getId())
            throw new VeiculoIndisponivelException(locacao.getVeiculo().getPlaca(), SituacaoVeiculo.LOCADO.toString());
    }
}
