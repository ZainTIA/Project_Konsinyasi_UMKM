package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginForm {
    private final JFrame frame;
    private final JTextField txtUser = new JTextField("admin");
    private final JPasswordField txtPass = new JPasswordField("admin123");
    private final JButton btnLogin = new JButton("Login & Hubungkan Database");

    public LoginForm() {
        frame = new JFrame("Login - UMKM");
        frame.setSize(400, 340);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("Masuk ke Dashboard UMKM ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel desc = new JLabel("Login Admin (admin / admin123)");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(100, 116, 139));
        header.add(title);
        header.add(desc);

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(18, 0, 18, 0));
        form.add(new JLabel("Username:"));
        form.add(txtUser);
        form.add(new JLabel("Password:"));
        form.add(txtPass);

        btnLogin.setBackground(new Color(15, 118, 110));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        main.add(btnLogin, BorderLayout.SOUTH);
        frame.add(main);
    }

    public JFrame getFrame() { return frame; }
    public void show() { frame.setVisible(true); }
    public void close() { frame.dispose(); }
    public String getUsername() { return txtUser.getText().trim(); }
    public String getPassword() { return new String(txtPass.getPassword()); }
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
        txtPass.addActionListener(listener);
    }
}