package umkm;

import Controller.LoginController;
import View.LoginForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginForm loginView = new LoginForm();
            new LoginController(loginView);
            loginView.show();
            
        });
    }
}