package cooked;

public class Resep {
    private int id;
    private String judul;
    private String deskripsi;
    private String bahan;
    private String langkah;
    private String kategori;
    private String gambarFilename;
    private int jumlahLike;
    private int penulisId;
    private String penulis;

    public Resep() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getBahan() { return bahan; }
    public void setBahan(String bahan) { this.bahan = bahan; }
    public String getLangkah() { return langkah; }
    public void setLangkah(String langkah) { this.langkah = langkah; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getGambarFilename() { return gambarFilename; }
    public void setGambarFilename(String gambarFilename) { this.gambarFilename = gambarFilename; }
    public int getJumlahLike() { return jumlahLike; }
    public void setJumlahLike(int jumlahLike) { this.jumlahLike = jumlahLike; }
    public int getPenulisId() { return penulisId; }
    public void setPenulisId(int penulisId) { this.penulisId = penulisId; }
    
    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }
}