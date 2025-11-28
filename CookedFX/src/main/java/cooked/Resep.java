    package cooked;



public class Resep {
    private int resepId;
    private String judul;
    private String deskripsi;
    private String bahan; 
    private String langkah; 
    private int jumlahLike;
    private String penulisUsername; 
    private String gambarFilename; 

    
    public Resep(int resepId, String judul, String deskripsi, String bahan, String langkah, int jumlahLike, String penulisUsername, String gambarFilename) {
        this.resepId = resepId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.bahan = bahan;
        this.langkah = langkah;
        this.jumlahLike = jumlahLike;
        this.penulisUsername = penulisUsername;
        this.gambarFilename = gambarFilename;
    }

    
    public int getResepId() { return resepId; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public String getBahan() { return bahan; }
    public String getLangkah() { return langkah; }
    public int getJumlahLike() { return jumlahLike; }
    public String getPenulisUsername() { return penulisUsername; }
    public String getGambarFilename() { return gambarFilename; }

    
  
    public void tampilkan() {
        System.out.println("---------------------------------");
        System.out.println("ID Resep: " + resepId);
        System.out.println("Judul: " + judul);
        System.out.println("Oleh: " + penulisUsername);
        System.out.println("Deskripsi: " + deskripsi);
        System.out.println("Likes: " + jumlahLike);
        System.out.println("File Gambar: " + (gambarFilename != null ? gambarFilename : "N/A"));
        
        System.out.println("\nBahan-bahan:");
        
        if(bahan != null) {
            for(String b : bahan.split("\n")) {
                System.out.println("- " + b);
            }
        }

        System.out.println("\nLangkah-langkah:");
        if(langkah != null) {
            int i = 1;
            for(String l : langkah.split("\n")) {
                System.out.println(i + ". " + l);
                i++;
            }
        }
        System.out.println("---------------------------------");
    }
}