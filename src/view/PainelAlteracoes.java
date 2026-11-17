package view;

import model.Alteracao;
import model.CategoriaAlteracao;
import model.Formato;
import model.SituacaoVeiculo;
import model.TipoAlteracao;
import service.Locadora;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
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
 * Tem uma aba de resumo com cartoes e uma aba para cada categoria com a lista da mais recente para a
 * mais antiga e os detalhes da linha escolhida Ela se atualiza sozinha a cada alteracao e nao e
 * bloqueada pelas janelas do menu O painel le o historico da Locadora e nao altera nenhum dado
 */
public class PainelAlteracoes {
    private static final String TITULO = "Painel de alterações";
    private static final String TODOS = "Todos os tipos";
    private static final String[] COLUNAS = {"Data e hora", "Tipo", "Registro", "Ação", "O que mudou"};
    private static final int[] LARGURAS = {170, 100, 190, 230, 380};
    private static final int COLUNA_TIPO = 1;
    private static final int ALTURA_LINHA = 30;
    private static final int RECENTES_NO_RESUMO = 8;

    private final Locadora locadora;
    private final JTabbedPane abas = new JTabbedPane();
    private final JLabel rodape = new JLabel();
    private final Runnable ouvinte = this::agendarAtualizacao;
    private JFrame janela;
    private boolean atualizacaoAgendada;

    // TODO: cria o painel de alteracoes ainda sem janela
    public PainelAlteracoes(Locadora locadora) {
        this.locadora = locadora;
        abas.setFont(Estilo.NEGRITO);
        rodape.setFont(Estilo.PEQUENA);
        rodape.setForeground(Estilo.TEXTO_SUAVE);
        rodape.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
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
        int largura = Math.min(tela.width, Math.max(860, Math.min(1100, tela.width * 55 / 100)));
        int altura = Math.min(760, tela.height);
        nova.setMinimumSize(new Dimension(Math.min(640, tela.width), Math.min(420, tela.height)));
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
                + "   |   atualiza sozinho a cada alteração");
    }

    // TODO: monta a aba de resumo com cartoes da situacao atual totais por categoria e ultimas alteracoes
    private JComponent montarResumo() {
        List<Alteracao> todas = locadora.getHistorico().listar();

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(secao("Situação atual", montarCartoes()));
        topo.add(Box.createVerticalStrut(14));
        topo.add(secao("Alterações por categoria", montarTabelaCategorias()));

        List<Alteracao> recentes = new ArrayList<>(todas);
        Collections.reverse(recentes);
        if (recentes.size() > RECENTES_NO_RESUMO)
            recentes = recentes.subList(0, RECENTES_NO_RESUMO);
        JComponent ultimas = recentes.isEmpty()
                ? rotuloVazio("Nenhuma alteração registrada ainda.")
                : new JScrollPane(montarTabelaRecentes(recentes));

        JPanel resumo = new JPanel(new BorderLayout(0, 14));
        resumo.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        resumo.add(topo, BorderLayout.NORTH);
        resumo.add(secao("Últimas alterações", ultimas), BorderLayout.CENTER);
        return resumo;
    }

    // TODO: monta os cartoes com veiculos por situacao locacoes ativas e em atraso
    private JComponent montarCartoes() {
        JPanel cartoes = new JPanel(new GridLayout(1, 5, 12, 0));
        for (SituacaoVeiculo situacao : SituacaoVeiculo.values()) {
            Color cor = switch (situacao) {
                case DISPONIVEL -> Estilo.VERDE;
                case LOCADO -> Estilo.PRIMARIA;
                case MANUTENCAO -> Estilo.LARANJA;
            };
            String legenda = switch (situacao) {
                case DISPONIVEL -> "Veículos disponíveis";
                case LOCADO -> "Veículos locados";
                case MANUTENCAO -> "Veículos em manutenção";
            };
            cartoes.add(cartao(locadora.getVeiculos().listarPorSituacao(situacao).size(), legenda, cor));
        }
        int atrasadas = locadora.listarAtrasadas(LocalDate.now()).size();
        cartoes.add(cartao(locadora.getLocacoes().listarAtivas().size(), "Locações ativas", Estilo.PRIMARIA));
        cartoes.add(cartao(atrasadas, "Locações em atraso", atrasadas > 0 ? Estilo.VERMELHO : Estilo.TEXTO_SUAVE));

        JLabel totais = new JLabel("Frota: " + locadora.getVeiculos().listar().size() + " veículo(s)   |   Clientes: "
                + locadora.getClientes().listar().size() + "   |   Funcionários: " + locadora.getFuncionarios().listar().size());
        totais.setFont(Estilo.PEQUENA);
        totais.setForeground(Estilo.TEXTO_SUAVE);

        JPanel bloco = new JPanel(new BorderLayout(0, 8));
        bloco.add(cartoes, BorderLayout.CENTER);
        bloco.add(totais, BorderLayout.SOUTH);
        return bloco;
    }

    // TODO: monta um cartao com um numero grande e a legenda
    private static JComponent cartao(int valor, String legenda, Color cor) {
        JLabel numero = new JLabel(String.valueOf(valor));
        numero.setFont(Estilo.DESTAQUE);
        numero.setForeground(cor);
        JLabel texto = new JLabel(legenda);
        texto.setFont(Estilo.PEQUENA);
        texto.setForeground(Estilo.TEXTO_SUAVE);

        JPanel cartao = new JPanel(new BorderLayout(0, 2));
        cartao.setBackground(Estilo.FUNDO_CARTAO);
        cartao.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        cartao.add(numero, BorderLayout.CENTER);
        cartao.add(texto, BorderLayout.SOUTH);
        return cartao;
    }

    // TODO: monta a tabela de inclusoes alteracoes e exclusoes de cada categoria
    private JComponent montarTabelaCategorias() {
        DefaultTableModel modelo = modeloSoLeitura(new String[]{"Categoria", "Inclusões", "Alterações", "Exclusões", "Última alteração"});
        for (CategoriaAlteracao categoria : CategoriaAlteracao.values()) {
            List<Alteracao> itens = locadora.getHistorico().listarPorCategoria(categoria);
            String ultima = itens.isEmpty() ? "-" : Formato.dataHora(itens.get(itens.size() - 1).getMomento());
            modelo.addRow(new Object[]{categoria, contar(itens, TipoAlteracao.INCLUSAO),
                    contar(itens, TipoAlteracao.ALTERACAO), contar(itens, TipoAlteracao.EXCLUSAO), ultima});
        }
        JTable tabela = criarTabela(modelo);
        for (int i = 1; i <= 3; i++)
            tabela.getColumnModel().getColumn(i).setCellRenderer(new RenderizadorLinha(SwingConstants.CENTER));
        return tabelaComCabecalho(tabela);
    }

    // TODO: monta a tabela das ultimas alteracoes de todas as categorias
    private JTable montarTabelaRecentes(List<Alteracao> recentes) {
        DefaultTableModel modelo = modeloSoLeitura(new String[]{"Data e hora", "Categoria", "Tipo", "Registro", "Ação"});
        for (Alteracao a : recentes)
            modelo.addRow(new Object[]{Formato.dataHora(a.getMomento()), a.getCategoria(), a.getTipo(), a.getRegistro(), a.getAcao()});
        JTable tabela = criarTabela(modelo);
        tabela.getColumnModel().getColumn(2).setCellRenderer(new RenderizadorTipo());
        int[] larguras = {170, 120, 100, 200, 260};
        for (int i = 0; i < larguras.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        return tabela;
    }

    // TODO: conta as alteracoes de um tipo
    private static long contar(List<Alteracao> itens, TipoAlteracao tipo) {
        return itens.stream().filter(a -> a.getTipo() == tipo).count();
    }

    // TODO: monta a aba de uma categoria com filtro por tipo tabela e detalhes
    private JComponent montarAba(List<Alteracao> itens) {
        if (itens.isEmpty())
            return rotuloVazio("Nenhuma alteração registrada nesta categoria.");
        List<Alteracao> recentes = new ArrayList<>(itens);
        Collections.reverse(recentes);

        DefaultTableModel modelo = modeloSoLeitura(COLUNAS);
        for (Alteracao a : recentes)
            modelo.addRow(new Object[]{Formato.dataHora(a.getMomento()), a.getTipo(), a.getRegistro(), a.getAcao(), resumo(a)});

        JTable tabela = criarTabela(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        contagem.setFont(Estilo.TEXTO);
        contagem.setForeground(Estilo.TEXTO_SUAVE);
        JComboBox<Object> filtro = new JComboBox<>();
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

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JLabel rotuloFiltro = new JLabel("Mostrar:");
        rotuloFiltro.setFont(Estilo.NEGRITO);
        topo.add(rotuloFiltro);
        topo.add(filtro);
        topo.add(contagem);

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabela), rolagem(detalhe));
        divisao.setResizeWeight(0.6);
        divisao.setBorder(null);
        JPanel aba = new JPanel(new BorderLayout());
        aba.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
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

    // TODO: cria um modelo de tabela que so mostra nao se edita
    private static DefaultTableModel modeloSoLeitura(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            // TODO: a tabela so mostra nao se edita
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    // TODO: cria a tabela no estilo do painel com linhas altas zebradas e cabecalho em negrito
    private static JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setFont(Estilo.TEXTO);
        tabela.setRowHeight(ALTURA_LINHA);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabela.setDefaultRenderer(Object.class, new RenderizadorLinha(SwingConstants.LEFT));
        tabela.getTableHeader().setFont(Estilo.NEGRITO);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, ALTURA_LINHA + 4));
        return tabela;
    }

    // TODO: junta o cabecalho e as linhas de uma tabela que nao fica dentro de rolagem
    private static JComponent tabelaComCabecalho(JTable tabela) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));
        painel.add(tabela.getTableHeader(), BorderLayout.NORTH);
        painel.add(tabela, BorderLayout.CENTER);
        return painel;
    }

    // TODO: poe um titulo de secao acima de um componente
    private static JComponent secao(String titulo, JComponent conteudo) {
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(Estilo.SECAO);
        rotulo.setForeground(Estilo.PRIMARIA);
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(conteudo, BorderLayout.CENTER);
        return painel;
    }

    // TODO: mostra uma mensagem centralizada quando nao ha nada para listar
    private static JComponent rotuloVazio(String texto) {
        JLabel vazio = new JLabel(texto, SwingConstants.CENTER);
        vazio.setFont(Estilo.TEXTO);
        vazio.setForeground(Estilo.TEXTO_SUAVE);
        return vazio;
    }

    // TODO: cria uma area de texto so de leitura no mesmo estilo das outras telas
    private static JTextArea areaDeTexto(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(Estilo.MONO);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setCaretPosition(0);
        return area;
    }

    // TODO: coloca o componente em uma area com barra de rolagem
    private static JScrollPane rolagem(JComponent componente) {
        JScrollPane painel = new JScrollPane(componente);
        painel.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));
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

    /** Celula com respiro lateral e linhas alternadas em duas cores sem mexer na linha escolhida */
    private static class RenderizadorLinha extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        // TODO: cria o renderizador com o alinhamento do texto
        RenderizadorLinha(int alinhamento) {
            setHorizontalAlignment(alinhamento);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        // TODO: alterna a cor de fundo das linhas que nao estao escolhidas
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            Component celula = super.getTableCellRendererComponent(tabela, valor, selecionada, false, linha, coluna);
            if (!selecionada) {
                celula.setBackground(linha % 2 == 0 ? Color.WHITE : Estilo.ZEBRA);
                celula.setForeground(Color.BLACK);
            }
            return celula;
        }
    }

    /** Pinta o tipo de verde inclusao laranja alteracao e vermelho exclusao em negrito */
    private static class RenderizadorTipo extends RenderizadorLinha {
        private static final long serialVersionUID = 1L;

        // TODO: cria o renderizador do tipo alinhado a esquerda
        RenderizadorTipo() {
            super(SwingConstants.LEFT);
        }

        // TODO: escolhe a cor do texto pelo tipo da alteracao
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            Component celula = super.getTableCellRendererComponent(tabela, valor, selecionada, foco, linha, coluna);
            celula.setFont(Estilo.NEGRITO);
            if (!selecionada && valor instanceof TipoAlteracao tipo) {
                celula.setForeground(switch (tipo) {
                    case INCLUSAO -> Estilo.VERDE;
                    case ALTERACAO -> Estilo.LARANJA;
                    case EXCLUSAO -> Estilo.VERMELHO;
                });
            }
            return celula;
        }
    }
}
