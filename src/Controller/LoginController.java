package Controller;

import View.FormUtama;
import View.LoginForm;
import javax.swing.JOptionPane;

public class LoginController {
    private final LoginForm view;

    public LoginController(LoginForm view) {
        this.view = view;
        this.view.addLoginListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String user = view.getUsername();
        String pass = view.getPassword();

        if (user.equals("admin") && pass.equals("admin123")) {
            view.close();
            FormUtama formUtama = new FormUtama();

            MitraController mitraCtrl = new MitraController(formUtama);
            ProdukController produkCtrl = new ProdukController(formUtama);
            TransaksiController trxCtrl = new TransaksiController(formUtama);

            Runnable refreshAll = () -> {
                mitraCtrl.loadData();
                produkCtrl.loadData();
                trxCtrl.loadData();
            };

            mitraCtrl.setOnDataChanged(refreshAll);
            produkCtrl.setOnDataChanged(refreshAll);
            trxCtrl.setOnDataChanged(refreshAll);

            formUtama.btnRefresh.addActionListener(ev -> refreshAll.run());
            formUtama.btnLogout.addActionListener(ev -> {
                formUtama.close();
                LoginForm loginBaru = new LoginForm();
                new LoginController(loginBaru);
                loginBaru.show();
            });

            refreshAll.run();
            formUtama.show();
        } else {
            JOptionPane.showMessageDialog(view.getFrame(), "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}