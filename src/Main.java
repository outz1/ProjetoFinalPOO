import exception.LocadoraException;
import service.Locadora;
import view.Estilo;
import view.PainelAlteracoes;
import view.TelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    // TODO: carrega os dados salvos abre o painel de alteracoes e a tela principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Estilo.aplicar();
            Locadora locadora = new Locadora();
            try {
                locadora.carregarDados();
            } catch (LocadoraException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Locadora de Veículos", JOptionPane.ERROR_MESSAGE);
            }
            PainelAlteracoes painel = new PainelAlteracoes(locadora);
            painel.abrir();
            new TelaPrincipal(locadora, painel).exibir();
            System.exit(0);
        });
    }
}
