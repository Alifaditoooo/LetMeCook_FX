package cooked;

public class Resep {
    private int id; // resep_id
    private String judul;
    private String deskripsi;
    
    private int penulisId;          // user_id_penulis
    private String penulisUsername; // Nama user (hasil JOIN)
    
    private String gambarFilename;
    private String bahan;
    private String langkah;
    private int jumlahLike;
    private boolean isDisukaiOlehSaya; 

    public Resep() {}

    // GETTER & SETTER LENGKAP
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPenulisId() { return penulisId; }
    public void setPenulisId(int penulisId) { this.penulisId = penulisId; }

    public String getPenulisUsername() { return penulisUsername; }
    public void setPenulisUsername(String penulisUsername) { this.penulisUsername = penulisUsername; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getGambarFilename() { return gambarFilename; }
    public void setGambarFilename(String gambarFilename) { this.gambarFilename = gambarFilename; }

    public String getBahan() { return bahan; }
    public void setBahan(String bahan) { this.bahan = bahan; }

    public String getLangkah() { return langkah; }
    public void setLangkah(String langkah) { this.langkah = langkah; }

    public int getJumlahLike() { return jumlahLike; }
    public void setJumlahLike(int jumlahLike) { this.jumlahLike = jumlahLike; }

    public boolean isDisukaiOlehSaya() { return isDisukaiOlehSaya; }
    public void setDisukaiOlehSaya(boolean disukaiOlehSaya) { this.isDisukaiOlehSaya = disukaiOlehSaya; }

    public void tambahLike() { this.jumlahLike++; }
}