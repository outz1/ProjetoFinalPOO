package view;

import model.Alteracao;
import model.CategoriaAlteracao;
import model.Formato;
import model.Locacao;
import model.SituacaoVeiculo;
import model.TipoAlteracao;
import service.Locadora;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Janela auxiliar que fica aberta ao lado do menu e mostra o que foi incluido alterado ou excluido
 * Tem uma aba de resumo e uma aba para cada categoria com a lista da mais recente para a mais antiga
 * e os detalhes da linha escolhida Ela se atualiza sozinha a cada alteracao e nao e bloqueada pelas
 * janelas do menu O painel le o historico da Locadora e nao altera nenhum dado
 */
public class PainelAlteracoes {
    private static final String TITULO = "Painel de alterações";
    private static final String TODOS = "Todos os tipos";
    private static final String[] COLUNAS = {"Data e hora", "Tipo", "Registro", "Ação", "O que mudou"};
    private static final int[] LARGURAS = {140, 75, 150, 190, 300};
    private static final int COLUNA_TIPO = 1;
    private static final int RECENTES_NO_RESUMO = 8;
    private static final Font FONTE = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private final Locadora locadora;
    private final JTabbedPane abas = new JTabbedPane();
    private final JLabel rodape = new JLabel();
    private final Runnable ouvinte = this::agendarAtualizacao;
    private JFrame janela;
    private boolean atualizacaoAgendada;

    // TODO: cria o painel de alteracoes ainda sem janela
    public PainelAlteracoes(Locadora locadora) {
        this.locadora = locadora;
        rodape.setFont(FONTE);
        rodape.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    // TODO: abre a janela do painel ou a traz para a frente se ja estiver aberta
    public void abrir() {
        if (janela == null)
            janela = criarJanela();
        atualizar();
        janela.setVisible(true);
        janela.setState(JFrame.NORMAL);
        janela.toFront();
    }

    // TODO: cria a janela no canto direito da tela que nao e bloqueada pelas janelas do menu
    private JFrame criarJanela() {
        JFrame nova = new JFrame(TITULO);
        // o menu abre janelas modais e sem isto elas travariam o painel enquanto estivessem abertas
        if (Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(Dialog.ModalExclusionType.APPLICATION_EXCLUDE))
            nova.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nova.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nova.add(abas, BorderLayout.CENTER);
        nova.add(rodape, BorderLayout.SOUTH);

        Rectangle tela = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int largura = Math.max(640, Math.min(900, tela.width * 45 / 100));
        int altura = Math.min(600, tela.height);
        nova.setMinimumSize(new Dimension(520, 360));
        nova.setBounds(tela.x + tela.width - largura, tela.y, largura, altura);

        locadora.getHistorico().adicionarOuvinte(ouvinte);
        nova.addWindowListener(new WindowAdapter() {
            // TODO: ao fechar para de ouvir o historico e o proximo abrir cria a janela de novo
            @Override
            public void windowClosed(WindowEvent e) {
                locadora.getHistorico().removerOuvinte(ouvinte);
                janela = null;
            }
        });
        return nova;
    }

    // TODO: junta varias alteracoes seguidas em uma unica atualizacao feita depois que a operacao termina
    private void agendarAtualizacao() {
        if (atualizacaoAgendada)
            return;
        atualizacaoAgendada = true;
        SwingUtilities.invokeLater(() -> {
            atualizacaoAgendada = false;
            if (janela != null)
                atualizar();
        });
    }

    // TODO: refaz o resumo e as abas mantendo a aba que estava aberta
    private void atualizar() {
        int escolhida = Math.max(0, abas.getSelectedIndex());
        abas.removeAll();
        abas.addTab("Resumo", montarResumo());
        for (CategoriaAlteracao categoria : CategoriaAlteracao.values()) {
            List<Alteracao> itens = locadora.getHistorico().listarPorCategoria(categoria);
            abas.addTab(categoria + " (" + itens.size() + ")", montarAba(itens));
        }
        abas.setSelectedIndex(Math.min(escolhida, abas.getTabCount() - 1));
        rodape.setText("Atualizado em " + Formato.dataHora(LocalDateTime.now())
                + "  |  atualiza sozinho a cada alteração");
    }

    // TODO: monta a aba de resumo com a situacao atual os totais por categoria e as ultimas alteracoes
    private JComponent montarResumo() {
        List<Alteracao> todas = locadora.getHistorico().listar();
        StringBuilder texto = new StringBuilder();

        texto.append("SITUAÇÃO ATUAL\n");
        texto.append("  Veículos: ").append(locadora.getVeiculos().listar().size()).append(" (");
        SituacaoVeiculo[] situacoes = SituacaoVeiculo.values();
        for (int i = 0; i < situacoes.length; i++) {
            if (i > 0)
                texto.append(", ");
            texto.append(locadora.getVeiculos().listarPorSituacao(situacoes[i]).size()).append(" ")
                    .append(situacoes[i].toString().toLowerCase());
        }
        texto.append(")\n");
        List<Locacao> ativas = locadora.getLocacoes().listarAtivas();
        texto.append("  Locações ativas: ").append(ativas.size()).append(" (")
                .append(locadora.listarAtrasadas(LocalDate.now()).size()).append(" em atraso)\n");
        texto.append("  Clientes: ").append(locadora.getClientes().listar().size())
                .append("   Funcionários: ").append(locadora.getFuncionarios().listar().size()).append("\n\n");

        texto.append("ALTERAÇÕES REGISTRADAS: ").append(todas.size()).append("\n\n");
        texto.append(String.format("%-14s %10s %11s %10s   %s%n", "Categoria", "Inclusões", "Alterações", "Exclusões",
                "Última alteração"));
        texto.append("-".repeat(78)).append("\n");
        for (CategoriaAlteracao categoria : CategoriaAlteracao.values()) {
            List<Alteracao> itens = locadora.getHistorico().listarPorCategoria(categoria);
            String ultima = itens.isEmpty() ? "-" : Formato.dataHora(itens.get(itens.size() - 1).getMomento());
            texto.append(String.format("%-14s %10d %11d %10d   %s%n", categoria, contar(itens, TipoAlteracao.INCLUSAO),
                    contar(itens, TipoAlteracao.ALTERACAO), contar(itens, TipoAlteracao.EXCLUSAO), ultima));
        }

        texto.append("\nÚLTIMAS ALTERAÇÕES\n");
        if (todas.isEmpty()) {
            texto.append("  Nenhuma alteração registrada ainda.\n");
        } else {
            int inicio = Math.max(0, todas.size() - RECENTES_NO_RESUMO);
            for (int i = todas.size() - 1; i >= inicio; i--) {
                Alteracao a = todas.get(i);
                texto.append(String.format("  %s  %-12s %-10s %-22s %s%n", Formato.dataHora(a.getMomento()), a.getCategoria(),
                        a.getTipo(), a.getRegistro(), a.getAcao()));
            }
        }
        return rolagem(areaDeTexto(texto.toString()));
    }

    // TODO: conta as alteracoes de um tipo
    private static long contar(List<Alteracao> itens, TipoAlteracao tipo) {
        return itens.stream().filter(a -> a.getTipo() == tipo).count();
    }

    // TODO: monta a aba de uma categoria com filtro por tipo tabela e detalhes
    private JComponent montarAba(List<Alteracao> itens) {
        if (itens.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma alteração registrada nesta categoria.", SwingConstants.CENTER);
            vazio.setFont(FONTE);
            return vazio;
        }
        List<Alteracao> recentes = new ArrayList<>(itens);
        Collections.reverse(recentes);

        DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
            // TODO: a tabela so mostra nao se edita
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        for (Alteracao a : recentes)
            modelo.addRow(new Object[]{Formato.dataHora(a.getMomento()), a.getTipo(), a.getRegistro(), a.getAcao(), resumo(a)});

        JTable tabela = new JTable(modelo);
        tabela.setFont(FONTE);
        tabela.setRowHeight(20);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        for (int i = 0; i < LARGURAS.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(LARGURAS[i]);
        tabela.getColumnModel().getColumn(COLUNA_TIPO).setCellRenderer(new RenderizadorTipo());
        TableRowSorter<TableModel> ordenador = new TableRowSorter<>(modelo);
        tabela.setRowSorter(ordenador);

        JTextArea detalhe = areaDeTexto("");
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int linha = tabela.getSelectedRow();
            detalhe.setText(linha < 0 ? "Selecione uma linha para ver os detalhes."
                    : detalhar(recentes.get(tabela.convertRowIndexToModel(linha))));
            detalhe.setCaretPosition(0);
        });

        JLabel contagem = new JLabel();
        contagem.setFont(FONTE);
        JComboBox<Object> filtro = new JComboBox<>();
        filtro.setFont(FONTE);
        filtro.addItem(TODOS);
        for (TipoAlteracao tipo : TipoAlteracao.values())
            filtro.addItem(tipo);
        Runnable aplicarFiltro = () -> {
            Object escolhido = filtro.getSelectedItem();
            ordenador.setRowFilter(escolhido instanceof TipoAlteracao tipo ? new FiltroTipo(tipo) : null);
            contagem.setText(tabela.getRowCount() + " registro(s)");
            if (tabela.getRowCount() > 0)
                tabela.setRowSelectionInterval(0, 0);
            else
                tabela.clearSelection();
        };
        filtro.addActionListener(e -> aplicarFiltro.run());
        aplicarFiltro.run();

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel rotuloFiltro = new JLabel("Mostrar:");
        rotuloFiltro.setFont(FONTE);
        topo.add(rotuloFiltro);
        topo.add(filtro);
        topo.add(contagem);

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabela), rolagem(detalhe));
        divisao.setResizeWeight(0.65);
        JPanel aba = new JPanel(new BorderLayout());
        aba.add(topo, BorderLayout.NORTH);
        aba.add(divisao, BorderLayout.CENTER);
        return aba;
    }

    // TODO: resume o que mudou em uma linha para a tabela
    private static String resumo(Alteracao a) {
        return switch (a.getTipo()) {
            case ALTERACAO -> a.getDetalhes().replace("\n", "; ");
            case INCLUSAO -> "Registro novo (detalhes abaixo)";
            case EXCLUSAO -> "Registro removido (detalhes abaixo)";
        };
    }

    // TODO: monta o texto completo da alteracao escolhida
    private static String detalhar(Alteracao a) {
        String titulo = switch (a.getTipo()) {
            case ALTERACAO -> "O que mudou:";
            case INCLUSAO -> "Dados cadastrados:";
            case EXCLUSAO -> "Dados que o registro tinha:";
        };
        return "Quando:   " + Formato.dataHora(a.getMomento())
                + "\nRegistro: " + a.getRegistro()
                + "\nAção:     " + a.getAcao()
                + "\n\n" + titulo + "\n" + a.getDetalhes();
    }

    // TODO: cria uma area de texto so de leitura no mesmo estilo das outras telas
    private static JTextArea areaDeTexto(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(FONTE);
        area.setMargin(new Insets(6, 8, 6, 8));
        area.setCaretPosition(0);
        return area;
    }

    // TODO: coloca o componente em uma area com barra de rolagem
    private static JScrollPane rolagem(JComponent componente) {
        JScrollPane painel = new JScrollPane(componente);
        painel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return painel;
    }

    /** Deixa passar so as linhas do tipo escolhido */
    private static class FiltroTipo extends RowFilter<TableModel, Integer> {
        private final TipoAlteracao tipo;

        // TODO: cria o filtro do tipo informado
        FiltroTipo(TipoAlteracao tipo) {
            this.tipo = tipo;
        }

        // TODO: aceita a linha se for do tipo escolhido
        @Override
        public boolean include(Entry<? extends TableModel, ? extends Integer> linha) {
            return linha.getValue(COLUNA_TIPO) == tipo;
        }
    }

    /** Pinta o tipo de verde inclusao laranja alteracao e vermelho exclusao sem mexer na linha escolhida */
    private static class RenderizadorTipo extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        private static final Color VERDE = new Color(0x1B, 0x7F, 0x3B);
        private static final Color LARANJA = new Color(0xB3, 0x5C, 0x00);
        private static final Color VERMELHO = new Color(0xB0, 0x20, 0x20);

        // TODO: escolhe a cor do texto pelo tipo da alteracao
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            Component celula = super.getTableCellRendererComponent(tabela, valor, selecionada, foco, linha, coluna);
            if (!selecionada && valor instanceof TipoAlteracao tipo) {
                celula.setForeground(switch (tipo) {
                    case INCLUSAO -> VERDE;
                    case ALTERACAO -> LARANJA;
                    case EXCLUSAO -> VERMELHO;
                });
            }
            return celula;
        }
    }
}
