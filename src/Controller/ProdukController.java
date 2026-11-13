package Controller;

import Model.BaseEntity;
import Model.Connector;
import Model.Produk;
import View.FormUtama;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class ProdukController {
    private final FormUtama view;
    private Runnable onDataChanged;

    public ProdukController(FormUtama view) {
        this.view = view;
        this.view.btnTambahProduk.addActionListener(e -> showForm(null));
        this.view.btnEditProduk.addActionListener(e -> editSelected());
        this.view.btnHapusProduk.addActionListener(e -> deleteSelected());
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

    public void loadData() {
        view.modelProduk.setRowCount(0);
        String sql = "SELECT idProduk, namaProduk, jenisProduk, kadaluarsa FROM produk ORDER BY idProduk ASC";
        try (Connection conn = Connector.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Produk p = new Produk(
                    rs.getString("idProduk"),
                    rs.getString("namaProduk"),
                    rs.getString("jenisProduk"),
                    rs.getDate("kadaluarsa")
                );
                view.modelProduk.addRow(new Object[]{
                    p.getId(), p.getNamaProduk(), p.getJenisProduk(),
                    p.getKadaluarsa() != null ? p.getKadaluarsa().toString() : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Gagal memuat data Produk:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = view.tblProduk.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih baris produk terlebih dahulu!");
            return;
        }
        Produk p = new Produk(
            view.modelProduk.getValueAt(row, 0).toString(),
            view.modelProduk.getValueAt(row, 1).toString(),
            view.modelProduk.getValueAt(row, 2).toString(),
            Date.valueOf(view.modelProduk.getValueAt(row, 3).toString())
        );
        showForm(p);
    }

    private void showForm(Produk existing) {
        boolean isEdit = (existing != null);
        String nextId = String.format("PR-%03d", view.modelProduk.getRowCount() + 1);
        JTextField txtId = new JTextField(isEdit ? existing.getId() : nextId);
        JTextField txtNama = new JTextField(isEdit ? existing.getNamaProduk() : "");
        JTextField txtJenis = new JTextField(isEdit ? existing.getJenisProduk() : "");
        JTextField txtExp = new JTextField(isEdit ? existing.getKadaluarsa().toString() : LocalDate.now().plusDays(7).toString());

        Object[] form = {"ID Produk:", txtId, "Nama Produk:", txtNama, "Jenis Produk:", txtJenis, "Kadaluarsa (YYYY-MM-DD):", txtExp};
        if (JOptionPane.showConfirmDialog(view.getFrame(), form, isEdit ? "Edit Produk" : "Tambah Produk", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Produk baru = new Produk(txtId.getText(), txtNama.getText(), txtJenis.getText(), Date.valueOf(txtExp.getText().trim()));
                
                // Demonstrasi Polymorphism BaseEntity
                BaseEntity entity = baru;
                System.out.println("Menyimpan entitas: " + entity.getInfoRingkas());

                String sql = isEdit
                    ? "UPDATE produk SET idProduk=?, namaProduk=?, jenisProduk=?, kadaluarsa=? WHERE idProduk=?"
                    : "INSERT INTO produk (idProduk, namaProduk, jenisProduk, kadaluarsa) VALUES (?, ?, ?, ?)";
                try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, baru.getId());
                    ps.setString(2, baru.getNamaProduk());
                    ps.setString(3, baru.getJenisProduk());
                    ps.setDate(4, baru.getKadaluarsa());
                    if (isEdit) ps.setString(5, existing.getId());
                    ps.executeUpdate();
                    if (onDataChanged != null) onDataChanged.run();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menyimpan Produk:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int row = view.tblProduk.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih produk yang akan dihapus!");
            return;
        }
        String id = view.modelProduk.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(view.getFrame(), "Hapus produk " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM produk WHERE idProduk=?")) {
                ps.setString(1, id);
                ps.executeUpdate();
                if (onDataChanged != null) onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menghapus Produk:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}