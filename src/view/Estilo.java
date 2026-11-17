package view;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Aparencia unica do programa tema Nimbus fontes maiores e as cores usadas pelo menu e pelo painel
 * de alteracoes Tudo que mexe em fonte ou cor passa por aqui para as janelas ficarem iguais
 */
public final class Estilo {
    public static final Font TEXTO = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font NEGRITO = TEXTO.deriveFont(Font.BOLD);
    public static final Font MENU = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    public static final Font SECAO = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    public static final Font DESTAQUE = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    public static final Font PEQUENA = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    public static final Color PRIMARIA = new Color(0x1F, 0x4E, 0x8C);
    public static final Color FUNDO_CARTAO = new Color(0xF3, 0xF6, 0xFA);
    public static final Color ZEBRA = new Color(0xF7, 0xF9, 0xFC);
    public static final Color BORDA = new Color(0xCF, 0xD8, 0xE3);
    public static final Color TEXTO_SUAVE = new Color(0x5A, 0x64, 0x72);
    public static final Color VERDE = new Color(0x1B, 0x7F, 0x3B);
    public static final Color LARANJA = new Color(0xB3, 0x5C, 0x00);
    public static final Color VERMELHO = new Color(0xB0, 0x20, 0x20);

    // TODO: classe utilitaria nao instanciavel
    private Estilo() {
    }

    // TODO: liga o tema Nimbus e as fontes maiores antes de criar qualquer janela
    public static void aplicar() {
        // as cores do Nimbus precisam ser definidas antes de ele ser instalado
        UIManager.put("nimbusBase", PRIMARIA);
        UIManager.put("nimbusSelectionBackground", PRIMARIA);
        try {
            for (UIManager.LookAndFeelInfo tema : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(tema.getName())) {
                    UIManager.setLookAndFeel(tema.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // sem o Nimbus segue com o visual padrao do Java so com as fontes maiores
        }
        UIManager.getLookAndFeelDefaults().put("defaultFont", TEXTO);
        for (String chave : new String[]{"Label.font", "Button.font", "TextField.font", "TextArea.font", "ComboBox.font",
                "List.font", "Table.font", "TabbedPane.font", "OptionPane.messageFont", "OptionPane.buttonFont",
                "CheckBox.font", "ToolTip.font"})
            UIManager.put(chave, TEXTO);
        UIManager.put("TableHeader.font", NEGRITO);
        UIManager.put("TextArea.font", MONO);
    }
}
