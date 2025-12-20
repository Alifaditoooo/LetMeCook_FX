package cooked;

public class Resep {
    private int id; // Jika pakai database, ini ID auto increment
    private String judul;
    private String deskripsi;
    private String penulisUsername;
    private String gambarFilename;
    
    // FIELD BARU
    private String bahan;
    private String langkah;
    private int jumlahLike;
    private boolean isDisukaiOlehSaya;

    // 1. CONSTRUCTOR KOSONG (Wajib ada untuk UploadController)
    public Resep() {
    }

    // 2. CONSTRUCTOR LENGKAP (Opsional, jika dipakai database lama)
    public Resep(int id, String judul, String deskripsi, String penulisUsername, String gambarFilename) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.penulisUsername = penulisUsername;
        this.gambarFilename = gambarFilename;
    }

    // --- GETTER & SETTER LAMA ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getPenulisUsername() { return penulisUsername; }
    public void setPenulisUsername(String penulisUsername) { this.penulisUsername = penulisUsername; }

    public String getGambarFilename() { return gambarFilename; }
    public void setGambarFilename(String gambarFilename) { this.gambarFilename = gambarFilename; }

    // --- GETTER & SETTER BARU (YANG BIKIN ERROR) ---
    
    public String getBahan() { return bahan; }
    public void setBahan(String bahan) { this.bahan = bahan; }

    public String getLangkah() { return langkah; }
    public void setLangkah(String langkah) { this.langkah = langkah; }

    public int getJumlahLike() { return jumlahLike; }
    public void setJumlahLike(int jumlahLike) { this.jumlahLike = jumlahLike; }

    public boolean isDisukaiOlehSaya() { return isDisukaiOlehSaya; }
    public void setDisukaiOlehSaya(boolean disukaiOlehSaya) { this.isDisukaiOlehSaya = disukaiOlehSaya; }

    // Method Logika Tambahan
    public void tambahLike() {
        this.jumlahLike++;
    }
}