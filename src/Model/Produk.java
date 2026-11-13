package Model;

import java.sql.Date;

public class Produk extends BaseEntity {
    private String namaProduk;
    private String jenisProduk;
    private Date kadaluarsa;

    public Produk(String idProduk, String namaProduk, String jenisProduk, Date kadaluarsa) {
        super(idProduk);
        setNamaProduk(namaProduk);
        setJenisProduk(jenisProduk);
        setKadaluarsa(kadaluarsa);
    }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) {
        if (namaProduk == null || namaProduk.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Produk wajib diisi!");
        }
        this.namaProduk = namaProduk.trim();
    }

    public String getJenisProduk() { return jenisProduk; }
    public void setJenisProduk(String jenisProduk) {
        if (jenisProduk == null || jenisProduk.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis Produk wajib diisi!");
        }
        this.jenisProduk = jenisProduk.trim();
    }

    public Date getKadaluarsa() { return kadaluarsa; }
    public void setKadaluarsa(Date kadaluarsa) { this.kadaluarsa = kadaluarsa; }

    @Override
    public String getInfoRingkas() {
        return getId() + " - " + namaProduk + " [" + jenisProduk + "]";
    }
}