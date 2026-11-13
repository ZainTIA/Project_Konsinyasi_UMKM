package Model;

import java.sql.Date;

public abstract class Transaksi extends BaseEntity implements HitungKeuangan {
    private String idMitra;
    private String idProduk;
    private Date tglTransaksi;

    public Transaksi(String idTransaksi, String idMitra, String idProduk, Date tglTransaksi) {
        super(idTransaksi);
        setIdMitra(idMitra);
        setIdProduk(idProduk);
        setTglTransaksi(tglTransaksi);
    }

    public String getIdMitra() { return idMitra; }
    public void setIdMitra(String idMitra) { this.idMitra = idMitra; }

    public String getIdProduk() { return idProduk; }
    public void setIdProduk(String idProduk) { this.idProduk = idProduk; }

    public Date getTglTransaksi() { return tglTransaksi; }
    public void setTglTransaksi(Date tglTransaksi) { this.tglTransaksi = tglTransaksi; }
}