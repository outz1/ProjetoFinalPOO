package repository;

import exception.LocacaoAtivaExistenteException;
import exception.LocadoraException;
import exception.VeiculoIndisponivelException;
import model.Locacao;
import model.SituacaoLocacao;
import model.SituacaoVeiculo;
import java.util.List;

public class LocacaoRepositorio extends RepositorioBase<Locacao> {
    public LocacaoRepositorio() {
        super("locação");
    }

    public List<Locacao> listarPorCliente(int idCliente) {
        return itens.stream().filter(l -> l.getCliente().getId() == idCliente).toList();
    }

    public List<Locacao> listarPorVeiculo(int idVeiculo) {
        return itens.stream().filter(l -> l.getVeiculo().getId() == idVeiculo).toList();
    }

    public List<Locacao> listarAtivas() {
        return itens.stream().filter(l -> l.getSituacao() == SituacaoLocacao.ATIVA).toList();
    }

    /** Devolve a locação ativa do cliente, ou null se ele não tiver nenhuma. */
    public Locacao buscarAtivaPorCliente(int idCliente) {
        for (Locacao l : itens) {
            if (l.getSituacao() == SituacaoLocacao.ATIVA && l.getCliente().getId() == idCliente)
                return l;
        }
        return null;
    }

    /** Devolve a locação ativa do veículo, ou null se ele não estiver locado. */
    public Locacao buscarAtivaPorVeiculo(int idVeiculo) {
        for (Locacao l : itens) {
            if (l.getSituacao() == SituacaoLocacao.ATIVA && l.getVeiculo().getId() == idVeiculo)
                return l;
        }
        return null;
    }

    /** RN01 e RN04 como rede de segurança: nunca duas locações ativas para o mesmo cliente ou veículo. */
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
