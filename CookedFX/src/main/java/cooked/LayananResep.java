package cooked;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LayananResep {

    private static User currentUser;

    // --- AUTH ---
    public boolean login(String username, String password) {
        // Mencocokkan dengan tabel users di db_cooked (3).sql
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Menggunakan Constructor User(int, string, string)
                currentUser = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    public void logout() { currentUser = null; }
    public String getCurrentUsername() { return currentUser != null ? currentUser.getUsername() : "Tamu"; }

    // --- RESEP ---
    public List<Resep> getSemuaResep() {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.resep_id DESC";
        return fetchResepList(sql);
    }

    public void tambahResepBaru(Resep resep) {
        if (currentUser == null) return;
        String sql = "INSERT INTO resep (judul, deskripsi, user_id_penulis, gambar_filename, bahan, langkah, jumlahLike) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resep.getJudul());
            stmt.setString(2, resep.getDeskripsi());
            stmt.setInt(3, currentUser.getId());
            stmt.setString(4, resep.getGambarFilename());
            stmt.setString(5, resep.getBahan());
            stmt.setString(6, resep.getLangkah());
            stmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<Resep> getResepPopuler() {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.jumlahLike DESC";
        return fetchResepList(sql);
    }

    public List<Resep> getResepDisukai() {
        if (currentUser == null) return new ArrayList<>();
        // Menggunakan tabel LIKES (kolom: user_id, resep_id)
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "JOIN likes l ON r.resep_id = l.resep_id " +
                     "WHERE l.user_id = ?";
        return fetchResepListParam(sql, currentUser.getId());
    }

    public List<Resep> getResepTeman() {
        if (currentUser == null) return new ArrayList<>();
        // Pastikan tabel FOLLOWS sudah dibuat
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE r.user_id_penulis IN (SELECT followed_id FROM follows WHERE follower_id = ?) " +
                     "ORDER BY r.resep_id DESC";
        return fetchResepListParam(sql, currentUser.getId());
    }

    // --- LOGIKA LIKE & FOLLOW ---
    public void toggleLike(Resep resep) {
        if (currentUser == null) return;
        
        if (resep.isDisukaiOlehSaya()) {
            // UNLIKE
            try (Connection conn = Koneksi.getConnection()) {
                conn.createStatement().executeUpdate("DELETE FROM likes WHERE user_id=" + currentUser.getId() + " AND resep_id=" + resep.getId());
                conn.createStatement().executeUpdate("UPDATE resep SET jumlahLike = jumlahLike - 1 WHERE resep_id=" + resep.getId());
                resep.setDisukaiOlehSaya(false);
                resep.setJumlahLike(resep.getJumlahLike() - 1);
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            // LIKE
            try (Connection conn = Koneksi.getConnection()) {
                conn.createStatement().executeUpdate("INSERT INTO likes (user_id, resep_id) VALUES (" + currentUser.getId() + ", " + resep.getId() + ")");
                conn.createStatement().executeUpdate("UPDATE resep SET jumlahLike = jumlahLike + 1 WHERE resep_id=" + resep.getId());
                resep.setDisukaiOlehSaya(true);
                resep.tambahLike();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void tambahTeman(String usernameChef) {
        if (currentUser == null) return;
        int chefId = getUserIdByUsername(usernameChef);
        if (chefId == -1 || chefId == currentUser.getId()) return;

        try (Connection conn = Koneksi.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO follows (follower_id, followed_id) VALUES (?, ?)");
             stmt.setInt(1, currentUser.getId());
             stmt.setInt(2, chefId);
             stmt.executeUpdate();
        } catch (Exception e) { 
            // Jika error (misal tabel follows belum ada), print di console
            System.out.println("Gagal follow: " + e.getMessage());
        }
    }
    
    public boolean isTeman(String usernameChef) {
        if (currentUser == null) return false;
        int chefId = getUserIdByUsername(usernameChef);
        String sql = "SELECT follow_id FROM follows WHERE follower_id = ? AND followed_id = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, chefId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) { return false; }
    }
    
    public int getJumlahTeman() {
        if (currentUser == null) return 0;
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    // --- HELPER ---
    private int getUserIdByUsername(String username) {
        try (Connection conn = Koneksi.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("user_id");
        } catch (Exception e) {}
        return -1;
    }

    private List<Resep> fetchResepList(String sql) {
        List<Resep> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    private List<Resep> fetchResepListParam(String sql, int param) {
        List<Resep> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private Resep mapResep(ResultSet rs) throws Exception {
        Resep r = new Resep();
        r.setId(rs.getInt("resep_id"));
        r.setJudul(rs.getString("judul"));
        r.setDeskripsi(rs.getString("deskripsi"));
        r.setPenulisId(rs.getInt("user_id_penulis"));
        r.setPenulisUsername(rs.getString("nama_penulis"));
        r.setGambarFilename(rs.getString("gambar_filename"));
        r.setBahan(rs.getString("bahan"));
        r.setLangkah(rs.getString("langkah"));
        r.setJumlahLike(rs.getInt("jumlahLike"));
        
        if (currentUser != null) {
            // Cek Like
            String checkLike = "SELECT like_id FROM likes WHERE user_id = " + currentUser.getId() + " AND resep_id = " + r.getId();
            try (Connection conn = Koneksi.getConnection(); ResultSet rsLike = conn.createStatement().executeQuery(checkLike)) {
                if (rsLike.next()) r.setDisukaiOlehSaya(true);
            }
        }
        return r;
    }
}