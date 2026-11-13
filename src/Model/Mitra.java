package Model;

public class Mitra extends BaseEntity {
    private String namaMitra;
    private String noHP;
    private String status;

    public Mitra(String idMitra, String namaMitra, String noHP, String status) {
        super(idMitra);
        setNamaMitra(namaMitra);
        setNoHP(noHP);
        setStatus(status);
    }

    public String getNamaMitra() { return namaMitra; }
    public void setNamaMitra(String namaMitra) {
        if (namaMitra == null || namaMitra.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Mitra wajib diisi!");
        }
        this.namaMitra = namaMitra.trim();
    }

    public String getNoHP() { return noHP; }
    public void setNoHP(String noHP) {
        if (noHP == null || noHP.trim().isEmpty()) {
            throw new IllegalArgumentException("No. HP wajib diisi!");
        }
        this.noHP = noHP.trim();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String getInfoRingkas() {
        return getId() + " - " + namaMitra + " (" + status + ")";
    }
}