package view;

import exception.LocadoraException;
import model.Formato;
import service.Locadora;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Base de todas as telas: monta o menu em laço, trata os erros e oferece os métodos de leitura e
 * mensagem via JOptionPane. Cada tela concreta só diz qual é o seu título, as suas opções e o que
 * cada uma faz.
 */
public abstract class Tela {
    protected final Locadora locadora;

    protected Tela(Locadora locadora) {
        this.locadora = locadora;
    }

    protected abstract String titulo();

    /** Opções do menu, sem a de voltar, que a base acrescenta no fim. */
    protected abstract String[] opcoes();

    protected abstract void executar(String opcao) throws LocadoraException;

    protected String rotuloVoltar() {
        return "Voltar";
    }

    /** Mostra o menu até o usuário voltar. Erros viram mensagem e o programa continua. */
    public final void exibir() {
        while (true) {
            List<String> menu = new ArrayList<>(List.of(opcoes()));
            menu.add(rotuloVoltar());
            Object escolha = JOptionPane.showInputDialog(null, "Escolha uma opção:", titulo(),
                    JOptionPane.QUESTION_MESSAGE, null, menu.toArray(), menu.get(0));
            if (escolha == null || escolha.equals(rotuloVoltar()))
                return;
            try {
                executar(escolha.toString());
            } catch (OperacaoCanceladaException e) {
                // o usuário desistiu no meio da operação: volta ao menu
            } catch (LocadoraException e) {
                mostrarErro(e.getMessage());
            } catch (RuntimeException e) {
                e.printStackTrace();
                mostrarErro("Erro inesperado: " + e);
            }
        }
    }

    // ---- leitura

    protected String lerTexto(String mensagem) {
        return lerTexto(mensagem, "");
    }

    protected String lerTexto(String mensagem, String valorAtual) {
        Object resposta = JOptionPane.showInputDialog(null, mensagem, titulo(), JOptionPane.QUESTION_MESSAGE,
                null, null, valorAtual);
        if (resposta == null)
            throw new OperacaoCanceladaException();
        return resposta.toString().trim();
    }

    protected int lerInteiro(String mensagem) throws LocadoraException {
        return lerInteiro(mensagem, "");
    }

    protected int lerInteiro(String mensagem, int valorAtual) throws LocadoraException {
        return lerInteiro(mensagem, String.valueOf(valorAtual));
    }

    private int lerInteiro(String mensagem, String valorAtual) throws LocadoraException {
        String texto = lerTexto(mensagem, valorAtual);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new LocadoraException("\"" + texto + "\" não é um número inteiro válido.");
        }
    }

    protected double lerDecimal(String mensagem) throws LocadoraException {
        return lerDecimal(mensagem, "");
    }

    protected double lerDecimal(String mensagem, double valorAtual) throws LocadoraException {
        return lerDecimal(mensagem, Formato.decimal(valorAtual));
    }

    private double lerDecimal(String mensagem, String valorAtual) throws LocadoraException {
        String texto = lerTexto(mensagem, valorAtual);
        // aceita 1.500,50 e 1500,50 (vírgula decimal) e também 1500.50
        String numero = texto.contains(",") ? texto.replace(".", "").replace(",", ".") : texto;
        try {
            return Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            throw new LocadoraException("\"" + texto + "\" não é um número válido.");
        }
    }

    protected LocalDate lerData(String mensagem) throws LocadoraException {
        return lerData(mensagem, null);
    }

    protected LocalDate lerData(String mensagem, LocalDate valorAtual) throws LocadoraException {
        String padrao = valorAtual == null ? "" : Formato.data(valorAtual);
        return Formato.lerData(lerTexto(mensagem + " (dd/mm/aaaa)", padrao));
    }

    protected boolean lerSimNao(String mensagem) {
        return JOptionPane.showConfirmDialog(null, mensagem, titulo(), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Devolve o índice da opção escolhida. */
    protected int lerOpcao(String mensagem, String[] opcoes) {
        Object escolha = JOptionPane.showInputDialog(null, mensagem, titulo(), JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);
        if (escolha == null)
            throw new OperacaoCanceladaException();
        return Arrays.asList(opcoes).indexOf(escolha);
    }

    /** Deixa o usuário escolher um item de uma lista. O rótulo de cada item deve ser curto e único. */
    protected <T> T escolherItem(String mensagem, List<T> itens, Function<T, String> rotulo) {
        String[] textos = itens.stream().map(rotulo).toArray(String[]::new);
        return itens.get(lerOpcao(mensagem, textos));
    }

    // ---- mensagens

    protected void mostrar(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo(), JOptionPane.INFORMATION_MESSAGE);
    }

    protected void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo(), JOptionPane.ERROR_MESSAGE);
    }

    /** Mostra um registro, um campo por linha. */
    protected void mostrarDetalhe(String cabecalho, Object registro) {
        mostrarTexto(cabecalho, registro.toString().replace(" | ", "\n"));
    }

    protected void mostrarLista(String cabecalho, List<?> itens) {
        if (itens.isEmpty()) {
            mostrar("Nenhum registro encontrado.");
            return;
        }
        StringBuilder texto = new StringBuilder(itens.size() + " registro(s):\n\n");
        for (Object item : itens)
            texto.append(item).append("\n");
        mostrarTexto(cabecalho, texto.toString());
    }

    /** Texto longo, com barra de rolagem. */
    private void mostrarTexto(String cabecalho, String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setCaretPosition(0);
        JScrollPane painel = new JScrollPane(area);
        painel.setPreferredSize(new Dimension(900, 400));
        JOptionPane.showMessageDialog(null, painel, cabecalho, JOptionPane.PLAIN_MESSAGE);
    }
}
