package cooked;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LayananResep {

    private static User currentUser;

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
    public User getCurrentUser() { return currentUser; }

    public List<Resep> getSemuaResep() {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.resep_id DESC";
        return fetchResepList(sql);
    }

    public List<Resep> getResepPopuler() {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r JOIN users u ON r.user_id_penulis = u.user_id WHERE r.jumlahLike > 0 ORDER BY r.jumlahLike DESC";
        return fetchResepList(sql);
    }

    public List<Resep> getResepDisukai() {
        if (currentUser == null) return new ArrayList<>();
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "JOIN likes l ON r.resep_id = l.resep_id " +
                     "WHERE l.user_id = ?";
        return fetchResepListParam(sql, currentUser.getId());
    }

    // --- (METODE LAMA getResepTeman DIGANTI DENGAN LOGIKA BARU DI CONTROLLER) ---
    // Tapi kita biarkan jika masih dibutuhkan di tempat lain.
    public List<Resep> getResepTeman() {
        if (currentUser == null) return new ArrayList<>();
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE r.user_id_penulis IN (SELECT followed_id FROM follows WHERE follower_id = ?) " +
                     "ORDER BY r.resep_id DESC";
        return fetchResepListParam(sql, currentUser.getId());
    }
    
    // --- METODE BARU: AMBIL LIST USER TEMAN (UNTUK KARTU PROFIL) ---
    public List<User> getTemanList() {
        if (currentUser == null) return new ArrayList<>();
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.password FROM users u " +
                     "JOIN follows f ON u.user_id = f.followed_id " +
                     "WHERE f.follower_id = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // -------------------------------------------------------------

    public List<Resep> getResepByUsername(String username) {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE u.username = ? ORDER BY r.resep_id DESC";
        return fetchResepListStringParam(sql, username);
    }
    
    public List<Resep> cariResep(String keyword) {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE r.judul LIKE ? OR r.bahan LIKE ? ORDER BY r.resep_id DESC";
        List<Resep> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likePattern = "%" + keyword + "%";
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Resep> filterKategori(String kategori) {
        String sql = "SELECT r.*, u.username AS nama_penulis FROM resep r " +
                     "JOIN users u ON r.user_id_penulis = u.user_id " +
                     "WHERE r.kategori = ? ORDER BY r.resep_id DESC";
        return fetchResepListStringParam(sql, kategori);
    }

    public void tambahResepBaru(Resep resep) {
        if (currentUser == null) return;
        String sql = "INSERT INTO resep (judul, deskripsi, user_id_penulis, gambar_filename, bahan, langkah, jumlahLike, kategori) VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resep.getJudul());
            stmt.setString(2, resep.getDeskripsi());
            stmt.setInt(3, currentUser.getId());
            stmt.setString(4, resep.getGambarFilename());
            stmt.setString(5, resep.getBahan());
            stmt.setString(6, resep.getLangkah());
            stmt.setString(7, resep.getKategori());
            stmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void toggleLike(Resep resep) {
        if (currentUser == null) return;
        if (resep.isDisukaiOlehSaya()) {
            try (Connection conn = Koneksi.getConnection()) {
                conn.createStatement().executeUpdate("DELETE FROM likes WHERE user_id=" + currentUser.getId() + " AND resep_id=" + resep.getId());
                conn.createStatement().executeUpdate("UPDATE resep SET jumlahLike = jumlahLike - 1 WHERE resep_id=" + resep.getId());
                resep.setDisukaiOlehSaya(false);
                resep.setJumlahLike(resep.getJumlahLike() - 1);
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            try (Connection conn = Koneksi.getConnection()) {
                conn.createStatement().executeUpdate("INSERT INTO likes (user_id, resep_id) VALUES (" + currentUser.getId() + ", " + resep.getId() + ")");
                conn.createStatement().executeUpdate("UPDATE resep SET jumlahLike = jumlahLike + 1 WHERE resep_id=" + resep.getId());
                resep.setDisukaiOlehSaya(true);
                resep.tambahLike();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public boolean hapusResep(int resepId) {
        if (currentUser == null) return false;
        String sqlHapusLikes = "DELETE FROM likes WHERE resep_id = ?";
        String sqlHapusResep = "DELETE FROM resep WHERE resep_id = ? AND user_id_penulis = ?"; 

        try (Connection conn = Koneksi.getConnection()) {
            try (PreparedStatement stmtLikes = conn.prepareStatement(sqlHapusLikes)) {
                stmtLikes.setInt(1, resepId);
                stmtLikes.executeUpdate();
            }
            try (PreparedStatement stmtResep = conn.prepareStatement(sqlHapusResep)) {
                stmtResep.setInt(1, resepId);
                stmtResep.setInt(2, currentUser.getId());
                int rowsAffected = stmtResep.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void hapusTeman(String usernameChef) {
         if (currentUser == null) return;
        int chefId = getUserIdByUsername(usernameChef);
        try (Connection conn = Koneksi.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM follows WHERE follower_id=? AND followed_id=?");
             stmt.setInt(1, currentUser.getId());
             stmt.setInt(2, chefId);
             stmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
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
    
    public int getFollowerCount(String username) {
        int userId = getUserIdByUsername(username);
        String sql = "SELECT COUNT(*) FROM follows WHERE followed_id = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
    
    public int getFollowingCount(String username) {
        int userId = getUserIdByUsername(username);
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

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
    
    private List<Resep> fetchResepListStringParam(String sql, String param) {
        List<Resep> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
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
        r.setKategori(rs.getString("kategori"));
        
        if (currentUser != null) {
            String checkLike = "SELECT like_id FROM likes WHERE user_id = " + currentUser.getId() + " AND resep_id = " + r.getId();
            try (Connection conn = Koneksi.getConnection(); ResultSet rsLike = conn.createStatement().executeQuery(checkLike)) {
                if (rsLike.next()) r.setDisukaiOlehSaya(true);
            }
        }
        return r;
    }
}