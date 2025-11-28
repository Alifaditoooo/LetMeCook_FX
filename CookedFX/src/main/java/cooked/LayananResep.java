package cooked;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LayananResep {
    
    
    private final String UPLOAD_DIRECTORY = "C:\\CookedUploads\\";
    
    private Connection conn = Koneksi.getKoneksi(); // Ambil koneksi
    private User userSaatIni; // Tetap ada untuk melacak sesi

    public LayananResep() {
        this.userSaatIni = null; // Awalnya tidak ada yang login
    }
    
    public User getUserSaatIni() {
        return userSaatIni;
    }
    
    public void logout() {
        if(userSaatIni != null) {
            System.out.println("Logout berhasil, " + userSaatIni.getUsername());
            this.userSaatIni = null;
        }
    }

    
    public boolean login(String username, String password) {
       
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // login kalo bisa
                    int userId = rs.getInt("user_id");
                    String userNama = rs.getString("username");
                    
                    // nyimpen sesi masuk
                    this.userSaatIni = new User(userId, userNama); 
                    
                    System.out.println("Login berhasil! Selamat datang, " + userNama);
                    return true;
                } else {
                    // kalo gagal
                    System.out.println("Error: Username atau password salah.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // register ke sql nya awas lupa oi
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); 
            
            ps.executeUpdate();
            
            System.out.println("Registrasi berhasil untuk: " + username);
            return true;
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Error 'Unique constraint'
                System.out.println("Error: Username '" + username + "' sudah terdaftar!");
            } else {
                System.out.println("Error database saat register: " + e.getMessage());
            }
            return false;
        }
    }

    // buat resep baru nnti di oper ke sql 
    public boolean buatResep(String judul, String deskripsi, List<String> daftarBahan, List<String> langkahLangkah, File fileGambar) {
        if (userSaatIni == null) {
            System.out.println("Error: Anda harus login untuk membuat resep.");
            return false;
        }

        String namaFileUnik = null;
        
        // --- 1. PROSES UPLOAD FILE ---
        if (fileGambar != null) {
            try {
               
                String extension = fileGambar.getName().substring(fileGambar.getName().lastIndexOf("."));
                namaFileUnik = UUID.randomUUID().toString() + extension;
                
                Path pathTujuan = Paths.get(UPLOAD_DIRECTORY + namaFileUnik);
                
                
                Files.copy(fileGambar.toPath(), pathTujuan, StandardCopyOption.REPLACE_EXISTING);
                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error saat upload file! Resep dibatalkan.");
                return false; 
            }
        }
        
        //nyimpen ke data base
        
        
        String bahanText = String.join("\n", daftarBahan);
        String langkahText = String.join("\n", langkahLangkah);

        String sql = "INSERT INTO resep (user_id_penulis, judul, deskripsi, bahan, langkah, gambar_filename) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userSaatIni.getId());
            ps.setString(2, judul);
            ps.setString(3, deskripsi);
            ps.setString(4, bahanText); // nyimpen bahan jadi text
            ps.setString(5, langkahText); // sama aja tapi langkah
            ps.setString(6, namaFileUnik); // simpen nama  FILE (atau null)
            
            ps.executeUpdate();
            System.out.println("Resep '" + judul + "' berhasil disimpan ke database!");
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error database saat simpan resep: " + e.getMessage());
            // kalo gagal harus apus manual 
            // (Files.delete(pathTujuan))
            return false;
        }
    }

    // manggil semua menu get-
    public List<Resep> getSemuaResep() {
        List<Resep> daftarResep = new ArrayList<>();
        
        // Query ini menggabungkan tabel 'resep' dan 'users'
        String sql = "SELECT r.*, u.username AS penulis " +
                     "FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "ORDER BY r.resep_id DESC"; // Tampilkan yang terbaru dulu

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // Ambil semua data dari baris
                Resep resep = new Resep(
                    rs.getInt("resep_id"),
                    rs.getString("judul"),
                    rs.getString("deskripsi"),
                    rs.getString("bahan"),
                    rs.getString("langkah"),
                    rs.getInt("jumlahLike"),
                    rs.getString("penulis"), // 'penulis' dari 'AS penulis'
                    rs.getString("gambar_filename")
                );
                daftarResep.add(resep);
            }
            
        } catch (SQLException e) {
            System.out.println("Error mengambil semua resep: " + e.getMessage());
        }
        return daftarResep; 
    }

    // --- METHOD GET RESEP SAYA (BARU DENGAN SQL) ---
    public List<Resep> getResepSaya() {
        List<Resep> resepSaya = new ArrayList<>();
        if (userSaatIni == null) return resepSaya; 

        String sql = "SELECT r.*, u.username AS penulis " +
                     "FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE r.user_id_penulis = ? " + 
                     "ORDER BY r.resep_id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userSaatIni.getId()); 
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resep resep = new Resep(
                        rs.getInt("resep_id"),
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        rs.getString("bahan"),
                        rs.getString("langkah"),
                        rs.getInt("jumlahLike"),
                        rs.getString("penulis"),
                        rs.getString("gambar_filename")
                    );
                    resepSaya.add(resep);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error mengambil resep saya: " + e.getMessage());
        }
        return resepSaya;
    }
    
    
}