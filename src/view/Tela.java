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
 * Base de todas as telas monta o menu em laco trata os erros e oferece os metodos de leitura e
 * mensagem via JOptionPane Cada tela concreta so diz qual e o seu titulo as suas opcoes e o que
 * cada uma faz
 */
public abstract class Tela {
    protected final Locadora locadora;

    // TODO: guarda a locadora usada pelas telas
    protected Tela(Locadora locadora) {
        this.locadora = locadora;
    }

    // TODO: devolve o titulo da tela
    protected abstract String titulo();

    // TODO: devolve as opcoes do menu sem a de voltar
    protected abstract String[] opcoes();

    // TODO: executa a opcao escolhida no menu
    protected abstract void executar(String opcao) throws LocadoraException;

    // TODO: devolve o texto da opcao que fecha o menu
    protected String rotuloVoltar() {
        return "Voltar";
    }

    // TODO: mostra o menu em laco ate o usuario voltar tratando os erros
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
                // o usuario desistiu no meio da operacao volta ao menu
            } catch (LocadoraException e) {
                mostrarErro(e.getMessage());
            } catch (RuntimeException e) {
                e.printStackTrace();
                mostrarErro("Erro inesperado: " + e);
            }
        }
    }

    // TODO: pede um texto com valor sugerido opcional
    protected String lerTexto(String mensagem) {
        return lerTexto(mensagem, "");
    }

    // TODO: pede um texto com valor sugerido opcional
    protected String lerTexto(String mensagem, String valorAtual) {
        Object resposta = JOptionPane.showInputDialog(null, mensagem, titulo(), JOptionPane.QUESTION_MESSAGE,
                null, null, valorAtual);
        if (resposta == null)
            throw new OperacaoCanceladaException();
        return resposta.toString().trim();
    }

    // TODO: pede um numero inteiro
    protected int lerInteiro(String mensagem) throws LocadoraException {
        return lerInteiro(mensagem, "");
    }

    // TODO: pede um numero inteiro
    protected int lerInteiro(String mensagem, int valorAtual) throws LocadoraException {
        return lerInteiro(mensagem, String.valueOf(valorAtual));
    }

    // TODO: pede um numero inteiro
    private int lerInteiro(String mensagem, String valorAtual) throws LocadoraException {
        String texto = lerTexto(mensagem, valorAtual);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new LocadoraException("\"" + texto + "\" não é um número inteiro válido.");
        }
    }

    // TODO: pede um numero decimal aceita virgula ou ponto
    protected double lerDecimal(String mensagem) throws LocadoraException {
        return lerDecimal(mensagem, "");
    }

    // TODO: pede um numero decimal aceita virgula ou ponto
    protected double lerDecimal(String mensagem, double valorAtual) throws LocadoraException {
        return lerDecimal(mensagem, Formato.decimal(valorAtual));
    }

    // TODO: pede um numero decimal aceita virgula ou ponto
    private double lerDecimal(String mensagem, String valorAtual) throws LocadoraException {
        String texto = lerTexto(mensagem, valorAtual);
        // aceita 1,500,50 e 1500,50 virgula decimal e tambem 1500,50
        String numero = texto.contains(",") ? texto.replace(".", "").replace(",", ".") : texto;
        try {
            return Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            throw new LocadoraException("\"" + texto + "\" não é um número válido.");
        }
    }

    // TODO: pede uma data dd/mm/aaaa
    protected LocalDate lerData(String mensagem) throws LocadoraException {
        return lerData(mensagem, null);
    }

    // TODO: pede uma data dd/mm/aaaa
    protected LocalDate lerData(String mensagem, LocalDate valorAtual) throws LocadoraException {
        String padrao = valorAtual == null ? "" : Formato.data(valorAtual);
        return Formato.lerData(lerTexto(mensagem + " (dd/mm/aaaa)", padrao));
    }

    // TODO: pergunta sim ou nao
    protected boolean lerSimNao(String mensagem) {
        return JOptionPane.showConfirmDialog(null, mensagem, titulo(), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // TODO: deixa escolher uma opcao e devolve o indice
    protected int lerOpcao(String mensagem, String[] opcoes) {
        Object escolha = JOptionPane.showInputDialog(null, mensagem, titulo(), JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);
        if (escolha == null)
            throw new OperacaoCanceladaException();
        return Arrays.asList(opcoes).indexOf(escolha);
    }

    // TODO: deixa escolher um item de uma lista
    protected <T> T escolherItem(String mensagem, List<T> itens, Function<T, String> rotulo) {
        String[] textos = itens.stream().map(rotulo).toArray(String[]::new);
        return itens.get(lerOpcao(mensagem, textos));
    }

    // TODO: mostra uma mensagem informativa
    protected void mostrar(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo(), JOptionPane.INFORMATION_MESSAGE);
    }

    // TODO: mostra uma mensagem de erro
    protected void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo(), JOptionPane.ERROR_MESSAGE);
    }

    // TODO: mostra um registro um campo por linha
    protected void mostrarDetalhe(String cabecalho, Object registro) {
        mostrarTexto(cabecalho, registro.toString().replace(" | ", "\n"));
    }

    // TODO: mostra uma lista de registros com a contagem
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

    // TODO: mostra um texto longo com barra de rolagem
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
