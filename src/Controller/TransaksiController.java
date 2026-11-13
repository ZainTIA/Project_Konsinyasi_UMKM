package Controller;

import Model.Connector;
import Model.Transaksi;
import Model.TransaksiKonsinyasi;
import View.FormUtama;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class TransaksiController {
    private final FormUtama view;
    private Runnable onDataChanged;

    public TransaksiController(FormUtama view) {
        this.view = view;
        this.view.btnTambahTrx.addActionListener(e -> showForm(-1));
        this.view.btnEditTrx.addActionListener(e -> editSelected());
        this.view.btnHapusTrx.addActionListener(e -> deleteSelected());
        this.view.btnCetakLaporan.addActionListener(e -> cetakLaporan());
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

    public void loadData() {
        view.modelTransaksi.setRowCount(0);
        view.modelHistory.setRowCount(0);
        long totalPendapatan = 0;
        long totalKeuntungan = 0;

        String sql = "SELECT t.*, m.namaMitra, p.namaProduk " +
                     "FROM transaksi t " +
                     "LEFT JOIN mitra m ON t.idMitra = m.idMitra " +
                     "LEFT JOIN produk p ON t.idProduk = p.idProduk " +
                     "ORDER BY t.idTransaksi ASC";

        try (Connection conn = Connector.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int hargaTitip = rs.getInt("hargaTitip");
                int hargaJual = rs.getInt("hargaJual");
                int jmlTitip = rs.getInt("jumlahTitip");
                int terjual = rs.getInt("jumlahTerjual");
                int pendapatan = rs.getInt("pendapatan");
                int keuntungan = 0;
                try {
                    keuntungan = rs.getInt("keuntungan");
                } catch (SQLException ignored) {
                    keuntungan = terjual * (hargaJual - hargaTitip);
                }

                totalPendapatan += pendapatan;
                totalKeuntungan += keuntungan;

                Object[] row = new Object[]{
                    rs.getString("idTransaksi"),
                    rs.getString("idMitra"),
                    rs.getString("namaMitra") != null ? rs.getString("namaMitra") : rs.getString("idMitra"),
                    rs.getString("idProduk"),
                    rs.getString("namaProduk") != null ? rs.getString("namaProduk") : rs.getString("idProduk"),
                    rs.getDate("tglTransaksi") != null ? rs.getDate("tglTransaksi").toString() : "-",
                    hargaTitip,
                    hargaJual,
                    jmlTitip,
                    terjual,
                    rs.getInt("sisaStok"),
                    pendapatan,
                    keuntungan
                };
                view.modelTransaksi.addRow(row);
                view.modelHistory.addRow(row);
            }
            view.updateDashboardStats(view.modelMitra.getRowCount(), view.modelProduk.getRowCount(), totalPendapatan, totalKeuntungan);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Gagal memuat data Transaksi:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = view.tblTransaksi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih transaksi yang akan diupdate!");
            return;
        }
        showForm(row);
    }

    private void showForm(int editRow) {
        if (view.modelMitra.getRowCount() == 0 || view.modelProduk.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pastikan data Mitra dan Produk sudah tersedia!");
            return;
        }
        boolean isEdit = (editRow >= 0);
        String nextId = String.format("TR-%03d", view.modelTransaksi.getRowCount() + 1);

        JTextField txtId = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 0).toString() : nextId);
        if (isEdit) txtId.setEditable(false);

        JComboBox<String> cbMitra = new JComboBox<>();
        for (int i = 0; i < view.modelMitra.getRowCount(); i++) {
            cbMitra.addItem(view.modelMitra.getValueAt(i, 0) + " - " + view.modelMitra.getValueAt(i, 1));
            if (isEdit && view.modelMitra.getValueAt(i, 0).toString().equals(view.modelTransaksi.getValueAt(editRow, 1).toString())) {
                cbMitra.setSelectedIndex(i);
            }
        }

        JComboBox<String> cbProduk = new JComboBox<>();
        for (int i = 0; i < view.modelProduk.getRowCount(); i++) {
            cbProduk.addItem(view.modelProduk.getValueAt(i, 0) + " - " + view.modelProduk.getValueAt(i, 1));
            if (isEdit && view.modelProduk.getValueAt(i, 0).toString().equals(view.modelTransaksi.getValueAt(editRow, 3).toString())) {
                cbProduk.setSelectedIndex(i);
            }
        }

        JTextField txtTgl = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 5).toString() : LocalDate.now().toString());
        JTextField txtHargaTitip = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 6).toString() : "2500");
        JTextField txtHargaJual = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 7).toString() : "4000");
        JTextField txtTitip = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 8).toString() : "30");
        JTextField txtTerjual = new JTextField(isEdit ? view.modelTransaksi.getValueAt(editRow, 9).toString() : "0");

        Object[] form = {
            "ID Transaksi:", txtId, "Mitra:", cbMitra, "Produk:", cbProduk,
            "Tanggal (YYYY-MM-DD):", txtTgl, "Harga Titip:", txtHargaTitip,
            "Harga Jual:", txtHargaJual, "Jumlah Titip:", txtTitip, "Jumlah Terjual:", txtTerjual
        };

        if (JOptionPane.showConfirmDialog(view.getFrame(), form, isEdit ? "Update Transaksi" : "Tambah Transaksi", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String idMitra = cbMitra.getSelectedItem().toString().split(" - ")[0];
                String idProduk = cbProduk.getSelectedItem().toString().split(" - ")[0];

                Transaksi trx = new TransaksiKonsinyasi(
                    txtId.getText(), idMitra, idProduk, Date.valueOf(txtTgl.getText().trim()),
                    Integer.parseInt(txtHargaTitip.getText().trim()),
                    Integer.parseInt(txtHargaJual.getText().trim()),
                    Integer.parseInt(txtTitip.getText().trim()),
                    Integer.parseInt(txtTerjual.getText().trim())
                );

                System.out.println("Menyimpan transaksi: " + trx.getInfoRingkas());
                TransaksiKonsinyasi tk = (TransaksiKonsinyasi) trx;

                String sql = isEdit
                    ? "UPDATE transaksi SET idMitra=?, idProduk=?, tglTransaksi=?, hargaTitip=?, hargaJual=?, jumlahTitip=?, jumlahTerjual=?, sisaStok=?, pendapatan=?, keuntungan=? WHERE idTransaksi=?"
                    : "INSERT INTO transaksi (idTransaksi, idMitra, idProduk, tglTransaksi, hargaTitip, hargaJual, jumlahTitip, jumlahTerjual, sisaStok, pendapatan, keuntungan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (isEdit) {
                        ps.setString(1, trx.getIdMitra());
                        ps.setString(2, trx.getIdProduk());
                        ps.setDate(3, trx.getTglTransaksi());
                        ps.setInt(4, tk.getHargaTitip());
                        ps.setInt(5, tk.getHargaJual());
                        ps.setInt(6, tk.getJumlahTitip());
                        ps.setInt(7, tk.getJumlahTerjual());
                        ps.setInt(8, trx.hitungSisaStok());
                        ps.setInt(9, trx.hitungPendapatan());
                        ps.setInt(10, trx.hitungKeuntungan());
                        ps.setString(11, trx.getId());
                    } else {
                        ps.setString(1, trx.getId());
                        ps.setString(2, trx.getIdMitra());
                        ps.setString(3, trx.getIdProduk());
                        ps.setDate(4, trx.getTglTransaksi());
                        ps.setInt(5, tk.getHargaTitip());
                        ps.setInt(6, tk.getHargaJual());
                        ps.setInt(7, tk.getJumlahTitip());
                        ps.setInt(8, tk.getJumlahTerjual());
                        ps.setInt(9, trx.hitungSisaStok());
                        ps.setInt(10, trx.hitungPendapatan());
                        ps.setInt(11, trx.hitungKeuntungan());
                    }
                    ps.executeUpdate();
                    if (onDataChanged != null) onDataChanged.run();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menyimpan Transaksi:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int row = view.tblTransaksi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Pilih transaksi yang akan dihapus!");
            return;
        }
        String id = view.modelTransaksi.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(view.getFrame(), "Hapus transaksi " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = Connector.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM transaksi WHERE idTransaksi=?")) {
                ps.setString(1, id);
                ps.executeUpdate();
                if (onDataChanged != null) onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Gagal menghapus Transaksi:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cetakLaporan() {
        try {
            boolean printed = view.tblHistory.print(JTable.PrintMode.FIT_WIDTH,
                new java.text.MessageFormat("Laporan Transaksi Konsinyasi UMKM"),
                new java.text.MessageFormat("Halaman {0}"));
            if (printed) JOptionPane.showMessageDialog(view.getFrame(), "Laporan berhasil dicetak!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getFrame(), "Gagal mencetak laporan: " + ex.getMessage());
        }
    }
}