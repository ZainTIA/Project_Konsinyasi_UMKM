package Model;

import java.sql.Date;

public class TransaksiKonsinyasi extends Transaksi {
    private int hargaTitip;
    private int hargaJual;
    private int jumlahTitip;
    private int jumlahTerjual;

    public TransaksiKonsinyasi(String idTransaksi, String idMitra, String idProduk, Date tglTransaksi,
                               int hargaTitip, int hargaJual, int jumlahTitip, int jumlahTerjual) {
        super(idTransaksi, idMitra, idProduk, tglTransaksi);
        setHargaTitip(hargaTitip);
        setHargaJual(hargaJual);
        setJumlahTitip(jumlahTitip);
        setJumlahTerjual(jumlahTerjual);
    }

    public int getHargaTitip() { return hargaTitip; }
    public void setHargaTitip(int hargaTitip) {
        if (hargaTitip < 0) throw new IllegalArgumentException("Harga titip tidak boleh negatif!");
        this.hargaTitip = hargaTitip;
    }

    public int getHargaJual() { return hargaJual; }
    public void setHargaJual(int hargaJual) {
        if (hargaJual < hargaTitip) throw new IllegalArgumentException("Harga jual tidak boleh kurang dari harga titip!");
        this.hargaJual = hargaJual;
    }

    public int getJumlahTitip() { return jumlahTitip; }
    public void setJumlahTitip(int jumlahTitip) {
        if (jumlahTitip <= 0) throw new IllegalArgumentException("Jumlah titip harus lebih dari 0!");
        this.jumlahTitip = jumlahTitip;
    }

    public int getJumlahTerjual() { return jumlahTerjual; }
    public void setJumlahTerjual(int jumlahTerjual) {
        if (jumlahTerjual < 0 || jumlahTerjual > jumlahTitip) {
            throw new IllegalArgumentException("Jumlah terjual harus antara 0 hingga " + jumlahTitip + "!");
        }
        this.jumlahTerjual = jumlahTerjual;
    }

    @Override
    public int hitungSisaStok() {
        return (jumlahTerjual > 0) ? (jumlahTitip - jumlahTerjual) : 0;
    }

    @Override
    public int hitungPendapatan() {
        return jumlahTerjual * hargaTitip;
    }

    @Override
    public int hitungKeuntungan() {
        return jumlahTerjual * (hargaJual - hargaTitip);
    }

    @Override
    public String getInfoRingkas() {
        return "Konsinyasi " + getId() + " | Terjual: " + jumlahTerjual + "/" + jumlahTitip +
               " | Keuntungan: Rp" + hitungKeuntungan();
    }
}