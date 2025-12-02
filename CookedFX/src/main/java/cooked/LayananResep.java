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
    private Connection conn = Koneksi.getKoneksi();
    
    // WAJIB STATIC: Agar data user tersimpan meski pindah halaman
    private static User userSaatIni; 

    // --- PERBAIKAN 1: CONSTRUCTOR KOSONG ---
    public LayananResep() {
        // JANGAN ADA KODE: this.userSaatIni = null; DI SINI!
        // Biarkan kosong agar status login tidak ter-reset.
    }
    
    public User getUserSaatIni() {
        return userSaatIni;
    }
    
    public void logout() {
        if(userSaatIni != null) {
            System.out.println("Logout berhasil, " + userSaatIni.getUsername());
            userSaatIni = null; // Hapus user HANYA saat logout dipanggil
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userNama = rs.getString("username");
                    userSaatIni = new User(userId, userNama); // Simpan ke static
                    System.out.println("Login berhasil! Selamat datang, " + userNama);
                    return true;
                } else {
                    System.out.println("Error: Username atau password salah.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); 
            ps.executeUpdate();
            System.out.println("Registrasi berhasil untuk: " + username);
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { 
                System.out.println("Error: Username '" + username + "' sudah terdaftar!");
            } else {
                System.out.println("Error database saat register: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean buatResep(String judul, String deskripsi, List<String> daftarBahan, List<String> langkahLangkah, File fileGambar) {
        if (userSaatIni == null) {
            System.out.println("Error: Anda harus login untuk membuat resep.");
            return false;
        }

        String namaFileUnik = null;
        if (fileGambar != null) {
            try {
                Path folderUpload = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(folderUpload)) {
                    Files.createDirectories(folderUpload);
                }
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
        
        String bahanText = String.join("\n", daftarBahan);
        String langkahText = String.join("\n", langkahLangkah);

        String sql = "INSERT INTO resep (user_id_penulis, judul, deskripsi, bahan, langkah, gambar_filename) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userSaatIni.getId());
            ps.setString(2, judul);
            ps.setString(3, deskripsi);
            ps.setString(4, bahanText);
            ps.setString(5, langkahText);
            ps.setString(6, namaFileUnik);
            
            ps.executeUpdate();
            
            // --- PERBAIKAN 2: Syntax Error Diperbaiki ---
            System.out.println("Resep '" + judul + "' berhasil disimpan ke database!");
            
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error database saat simpan resep: " + e.getMessage());
            return false;
        }
    }

    public List<Resep> getSemuaResep() {
        List<Resep> daftarResep = new ArrayList<>();
        String sql = "SELECT r.*, u.username AS penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.resep_id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
                daftarResep.add(resep);
            }
        } catch (SQLException e) {
            System.out.println("Error mengambil semua resep: " + e.getMessage());
        }
        return daftarResep; 
    }

    public List<Resep> getResepSaya() {
        List<Resep> resepSaya = new ArrayList<>();
        if (userSaatIni == null) return resepSaya;

        String sql = "SELECT r.*, u.username AS penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id WHERE r.user_id_penulis = ? ORDER BY r.resep_id DESC";

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