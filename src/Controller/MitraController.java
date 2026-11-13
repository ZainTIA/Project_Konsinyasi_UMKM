package Controller;

import Model.BaseEntity;
import Model.Connector;
import Model.Mitra;
import View.FormUtama;
import javax.swing.*;
import java.sql.*;

public class MitraController {
    private final FormUtama view;
    private Runnable onDataChanged;

    public MitraController(FormUtama view) {
        this.view = view;
        this.view.btnTambahMitra.addActionListener(e -> showForm(null));
        this.view.btnEditMitra.addActionListener(e -> editSelected());
        this.view.btnHapusMitra.addActionListener(e -> deleteSelected());
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

    public void loadData() {
        view.modelMitra.setRowCount(0);
        String sql = "SELECT idMitra, namaMitra, noHP, status FROM mitra ORDER BY idMitra ASC";
        try (Connection conn = Connector.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Instansiasi Objek OOP Mitra
                Mitra m = new Mitra(
                    rs.getString("idMitra"),
                    rs.getString("namaMitra"),
                    rs.getString("noHP"),
                    rs.getString("status")
                );
                view.modelMitra.addRow(new Object[]{m.getId(), m.getNamaMitra(), m.getNoHP(), m.getStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Gagal memuat data Mitra:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = view.tblMitra.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih baris mitra terlebih dahulu!");
            return;
        }
        Mitra m = new Mitra(
            view.modelMitra.getValueAt(row, 0).toString(),
            view.modelMitra.getValueAt(row, 1).toString(),
            view.modelMitra.getValueAt(row, 2).toString(),
            view.modelMitra.getValueAt(row, 3).toString()
        );
        showForm(m);
    }

    private void showForm(Mitra existing) {
        boolean isEdit = (existing != null);
        String nextId = String.format("MT-%03d", view.modelMitra.getRowCount() + 1);
        JTextField txtId = new JTextField(isEdit ? existing.getId() : nextId);
        JTextField txtNama = new JTextField(isEdit ? existing.getNamaMitra() : "");
        JTextField txtHp = new JTextField(isEdit ? existing.getNoHP() : "");
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Aktif", "Nonaktif"});
        if (isEdit) cbStatus.setSelectedItem(existing.getStatus());

        Object[] form = {"ID Mitra:", txtId, "Nama Mitra:", txtNama, "No. HP:", txtHp, "Status:", cbStatus};
        if (JOptionPane.showConfirmDialog(view.getFrame(), form, isEdit ? "Edit Mitra" : "Tambah Mitra", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                // Validasi otomatis lewat Encapsulation Setter di Model Mitra
                Mitra baru = new Mitra(txtId.getText(), txtNama.getText(), txtHp.getText(), cbStatus.getSelectedItem().toString());
                
                // Demonstrasi Polymorphism BaseEntity
                BaseEntity entity = baru;
                System.out.println("Menyimpan entitas: " + entity.getInfoRingkas());

                String sql = isEdit
                    ? "UPDATE mitra SET idMitra=?, namaMitra=?, noHP=?, status=? WHERE idMitra=?"
                    : "INSERT INTO mitra (idMitra, namaMitra, noHP, status) VALUES (?, ?, ?, ?)";
                try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, baru.getId());
                    ps.setString(2, baru.getNamaMitra());
                    ps.setString(3, baru.getNoHP());
                    ps.setString(4, baru.getStatus());
                    if (isEdit) ps.setString(5, existing.getId());
                    ps.executeUpdate();
                    if (onDataChanged != null) onDataChanged.run();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menyimpan Mitra:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int row = view.tblMitra.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih mitra yang akan dihapus!");
            return;
        }
        String id = view.modelMitra.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(view.getFrame(), "Hapus mitra " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM mitra WHERE idMitra=?")) {
                ps.setString(1, id);
                ps.executeUpdate();
                if (onDataChanged != null) onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menghapus Mitra:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}