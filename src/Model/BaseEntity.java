package Model;

public abstract class BaseEntity {
    private String id;

    public BaseEntity(String id) {
        setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID tidak boleh kosong!");
        }
        this.id = id.trim();
    }

    // Kontrak abstrak untuk Polymorphism
    public abstract String getInfoRingkas();
}