package View;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

public class FormUtama {
    public static final Color PRIMARY = new Color(15, 118, 110);
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color BG_LIGHT = new Color(241, 245, 249);
    public static final Color TEXT_DARK = new Color(30, 41, 59);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color SUCCESS = new Color(22, 163, 74);

    private final JFrame frame;
    private CardLayout contentLayout;
    private JPanel contentPanel;

    public DefaultTableModel modelMitra, modelProduk, modelTransaksi, modelHistory;
    public JTable tblMitra, tblProduk, tblTransaksi, tblHistory;

    public JButton btnRefresh, btnLogout;
    public JButton btnTambahMitra, btnEditMitra, btnHapusMitra;
    public JButton btnTambahProduk, btnEditProduk, btnHapusProduk;
    public JButton btnTambahTrx, btnEditTrx, btnHapusTrx, btnCetakLaporan;

    private JLabel lblTotalMitra, lblTotalProduk, lblTotalPendapatan, lblTotalKeuntungan;
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public FormUtama() {
        frame = new JFrame("Sistem Manajemen Konsinyasi UMKM");
        frame.setSize(1280, 750);
        frame.setMinimumSize(new Dimension(1100, 640));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        initTables();
        frame.add(createSidebar(), BorderLayout.WEST);
        frame.add(createMainArea(), BorderLayout.CENTER);
    }

    public JFrame getFrame() { return frame; }
    public void show() { frame.setVisible(true); }
    public void close() { frame.dispose(); }

    private void initTables() {
        modelMitra = new DefaultTableModel(new String[]{"ID Mitra", "Nama Mitra", "No. HP", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modelProduk = new DefaultTableModel(new String[]{"ID Produk", "Nama Produk", "Jenis Produk", "Kadaluarsa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        String[] colsTrx = {
            "ID Transaksi", "ID Mitra", "Nama Mitra", "ID Produk", "Nama Produk",
            "Tgl Transaksi", "Harga Titip", "Harga Jual", "Titip", "Terjual",
            "Sisa Stok", "Pendapatan", "Keuntungan"
        };
        modelTransaksi = new DefaultTableModel(colsTrx, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modelHistory = new DefaultTableModel(colsTrx, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblMitra = createStyledTable(modelMitra);
        tblProduk = createStyledTable(modelProduk);
        tblTransaksi = createStyledTable(modelTransaksi);
        tblHistory = createStyledTable(modelHistory);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(SIDEBAR_BG);

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 22));
        brand.setBackground(SIDEBAR_BG);
        brand.setBorder(new MatteBorder(0, 0, 1, 0, SIDEBAR_HOVER));

        JLabel badge = new JLabel("U", SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(38, 38));
        badge.setOpaque(true);
        badge.setBackground(PRIMARY);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setOpaque(false);
        JLabel app = new JLabel("UMKM PRO");
        app.setForeground(Color.WHITE);
        app.setFont(new Font("Segoe UI", Font.BOLD, 16));   
        titleGroup.add(app);    

        brand.add(badge);
        brand.add(titleGroup);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(16, 12, 16, 12));

        String[][] menus = {
            {"DASHBOARD", "Dashboard Ringkasan"},
            {"MITRA", "Data Mitra"},
            {"PRODUK", "Data Produk"},
            {"TRANSAKSI", "Transaksi Konsinyasi"},
            {"HISTORY", "Riwayat & Laporan"}
        };
        for (String[] m : menus) {
            menuPanel.add(createNavButton(m[1], m[0]));
            menuPanel.add(Box.createVerticalStrut(6));
        }
        sidebar.add(menuPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(SIDEBAR_BG);
        bottom.setBorder(new EmptyBorder(12, 12, 18, 12));
        btnLogout = createActionButton("Keluar Aplikasi", SIDEBAR_HOVER);
        btnLogout.setForeground(new Color(248, 113, 113));
        bottom.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(SIDEBAR_BG);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(SIDEBAR_BG); }
        });
        btn.addActionListener(e -> contentLayout.show(contentPanel, card));
        return btn;
    }

    private JPanel createMainArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            new EmptyBorder(14, 24, 14, 24)
        ));
        JLabel lblTitle = new JLabel("Sistem Pencatatan Konsinyasi UMKM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);

        btnRefresh = createActionButton("Refresh Database", PRIMARY);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        contentPanel.add(createDashboardView(), "DASHBOARD");
        contentPanel.add(createMitraView(), "MITRA");
        contentPanel.add(createProdukView(), "PRODUK");
        contentPanel.add(createTransaksiView(), "TRANSAKSI");
        contentPanel.add(createHistoryView(), "HISTORY");

        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);

        lblTotalMitra = new JLabel("0");
        lblTotalProduk = new JLabel("0");
        lblTotalPendapatan = new JLabel("Rp 0");
        lblTotalKeuntungan = new JLabel("Rp 0");

        grid.add(createStatCard("Total Mitra", lblTotalMitra, new Color(37, 99, 235)));
        grid.add(createStatCard("Total Produk", lblTotalProduk, PRIMARY));
        grid.add(createStatCard("Total Pendapatan Mitra", lblTotalPendapatan, SUCCESS));
        grid.add(createStatCard("Total Keuntungan", lblTotalKeuntungan, WARNING));

        panel.add(grid, BorderLayout.NORTH);

        JPanel card = createCard("Daftar Transaksi Konsinyasi Terbaru");
        card.add(new JScrollPane(createStyledTable(modelTransaksi)), BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel val, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 4, 0, 0, accent),
            new EmptyBorder(18, 18, 18, 18)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(100, 116, 139));
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(TEXT_DARK);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMitraView() {
        JPanel card = createCard("Data Mitra (Tabel: mitra)");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        btnTambahMitra = createActionButton("+ Tambah Mitra", PRIMARY);
        btnEditMitra = createActionButton("Edit Mitra", WARNING);
        btnHapusMitra = createActionButton("Hapus", DANGER);
        toolbar.add(btnTambahMitra);
        toolbar.add(btnEditMitra);
        toolbar.add(btnHapusMitra);
        ((JPanel) card.getComponent(0)).add(toolbar, BorderLayout.EAST);
        card.add(new JScrollPane(tblMitra), BorderLayout.CENTER);
        return card;
    }

    private JPanel createProdukView() {
        JPanel card = createCard("Data Produk (Tabel: produk)");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        btnTambahProduk = createActionButton("+ Tambah Produk", PRIMARY);
        btnEditProduk = createActionButton("Edit Produk", WARNING);
        btnHapusProduk = createActionButton("Hapus", DANGER);
        toolbar.add(btnTambahProduk);
        toolbar.add(btnEditProduk);
        toolbar.add(btnHapusProduk);
        ((JPanel) card.getComponent(0)).add(toolbar, BorderLayout.EAST);
        card.add(new JScrollPane(tblProduk), BorderLayout.CENTER);
        return card;
    }

    private JPanel createTransaksiView() {
        JPanel card = createCard("Transaksi Konsinyasi (Tabel: transaksi)");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        btnTambahTrx = createActionButton("+ Tambah Transaksi", PRIMARY);
        btnEditTrx = createActionButton("Update Penjualan", WARNING);
        btnHapusTrx = createActionButton("Hapus", DANGER);
        toolbar.add(btnTambahTrx);
        toolbar.add(btnEditTrx);
        toolbar.add(btnHapusTrx);
        ((JPanel) card.getComponent(0)).add(toolbar, BorderLayout.EAST);
        card.add(new JScrollPane(tblTransaksi), BorderLayout.CENTER);
        return card;
    }

    private JPanel createHistoryView() {
        JPanel card = createCard("Laporan Keseluruhan Transaksi Konsinyasi");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        btnCetakLaporan = createActionButton("Cetak Laporan (Print / PDF)", PRIMARY);
        toolbar.add(btnCetakLaporan);
        ((JPanel) card.getComponent(0)).add(toolbar, BorderLayout.EAST);
        card.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(TEXT_DARK);
        top.add(lbl, BorderLayout.WEST);
        card.add(top, BorderLayout.NORTH);
        return card;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
        JTableHeader h = table.getTableHeader();
        h.setBackground(new Color(248, 250, 252));
        h.setForeground(TEXT_DARK);
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setPreferredSize(new Dimension(0, 36));
        return table;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateDashboardStats(int jmlMitra, int jmlProduk, long pendapatan, long keuntungan) {
        lblTotalMitra.setText(String.valueOf(jmlMitra));
        lblTotalProduk.setText(String.valueOf(jmlProduk));
        lblTotalPendapatan.setText(rupiah.format(pendapatan));
        lblTotalKeuntungan.setText(rupiah.format(keuntungan));
    }
}