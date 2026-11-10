import exception.LocadoraException;
import service.Locadora;
import view.TelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Locadora locadora = new Locadora();
            try {
                locadora.carregarDados();
            } catch (LocadoraException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Locadora de Veículos", JOptionPane.ERROR_MESSAGE);
            }
            new TelaPrincipal(locadora).exibir();
            System.exit(0);
        });
    }
}
