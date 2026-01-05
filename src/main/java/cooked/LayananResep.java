package cooked;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LayananResep {

    public boolean login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id")); 
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                App.currentUser = user;
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean register(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }
    
    public User getCurrentUser() { 
        return App.currentUser; 
    }
    
    public String getCurrentUsername() { 
        return (App.currentUser != null) ? App.currentUser.getUsername() : ""; 
    }
    
    public void logout() { 
        App.currentUser = null; 
        App.resepDetail = null; 
    }

    public void tambahResepBaru(Resep resep) {
        if (App.currentUser == null) return;
        String query = "INSERT INTO resep (judul, deskripsi, bahan, langkah, kategori, gambar_filename, user_id_penulis, jumlahLike) VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, resep.getJudul());
            ps.setString(2, resep.getDeskripsi());
            ps.setString(3, resep.getBahan());
            ps.setString(4, resep.getLangkah());
            ps.setString(5, resep.getKategori());
            ps.setString(6, resep.getGambarFilename());
            ps.setInt(7, App.currentUser.getId());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Resep mapResultSetToResep(ResultSet rs) throws Exception {
        Resep r = new Resep();
        r.setId(rs.getInt("resep_id")); 
        r.setJudul(rs.getString("judul"));
        r.setDeskripsi(rs.getString("deskripsi"));
        r.setBahan(rs.getString("bahan"));
        r.setLangkah(rs.getString("langkah"));
        r.setKategori(rs.getString("kategori"));
        r.setGambarFilename(rs.getString("gambar_filename"));
        r.setJumlahLike(rs.getInt("jumlahLike"));
        r.setPenulisId(rs.getInt("user_id_penulis"));
        try { r.setPenulis(rs.getString("username")); } catch (Exception e) { r.setPenulis("Unknown"); }
        return r;
    }

    public List<Resep> getSemuaResep() {
        List<Resep> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM resep r LEFT JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.resep_id DESC";
        try (Connection conn = Koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Resep> getResepPopuler() {
        List<Resep> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM resep r LEFT JOIN users u ON r.user_id_penulis = u.user_id ORDER BY r.jumlahLike DESC";
        try (Connection conn = Koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Resep> getResepDisukai() {
        List<Resep> list = new ArrayList<>();
        if (App.currentUser == null) return list;
        String query = "SELECT r.*, u.username FROM resep r JOIN users u ON r.user_id_penulis = u.user_id JOIN likes l ON r.resep_id = l.resep_id WHERE l.user_id = ? ORDER BY r.resep_id DESC";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, App.currentUser.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Resep> cariResep(String keyword) {
        List<Resep> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM resep r LEFT JOIN users u ON r.user_id_penulis = u.user_id WHERE r.judul LIKE ? ORDER BY r.resep_id DESC";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Resep> getResepByKategori(String kategori) {
        List<Resep> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM resep r LEFT JOIN users u ON r.user_id_penulis = u.user_id WHERE r.kategori = ? ORDER BY r.resep_id DESC";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, kategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Resep> getResepByUsername(String usernameTarget) {
        List<Resep> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM resep r JOIN users u ON r.user_id_penulis = u.user_id WHERE u.username = ? ORDER BY r.resep_id DESC";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, usernameTarget);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToResep(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void hapusResep(int resepId) {
        String query = "DELETE FROM resep WHERE resep_id = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, resepId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean isResepDisukai(int resepId, int userId) {
        String query = "SELECT * FROM likes WHERE resep_id = ? AND user_id = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, resepId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean toggleLike(int resepId, int userId) {
        String checkQuery = "SELECT * FROM likes WHERE resep_id = ? AND user_id = ?";
        String insertLike = "INSERT INTO likes (resep_id, user_id) VALUES (?, ?)";
        String updateCount = "UPDATE resep SET jumlahLike = jumlahLike + 1 WHERE resep_id = ?";
        try (Connection conn = Koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psCheck = conn.prepareStatement(checkQuery);
                psCheck.setInt(1, resepId); psCheck.setInt(2, userId);
                if (psCheck.executeQuery().next()) return false;
                PreparedStatement psInsert = conn.prepareStatement(insertLike);
                psInsert.setInt(1, resepId); psInsert.setInt(2, userId);
                psInsert.executeUpdate();
                PreparedStatement psUpdate = conn.prepareStatement(updateCount);
                psUpdate.setInt(1, resepId);
                psUpdate.executeUpdate();
                conn.commit();
                return true;
            } catch (Exception ex) { conn.rollback(); ex.printStackTrace(); }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getDaftarFollowing() {
        List<User> list = new ArrayList<>();
        if (App.currentUser == null) return list;
        String query = "SELECT u.user_id, u.username FROM users u JOIN follows f ON u.user_id = f.followed_id WHERE f.follower_id = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, App.currentUser.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean isTeman(String targetUsername) {
        if(App.currentUser == null) return false;
        String query = "SELECT * FROM follows f JOIN users u ON f.followed_id = u.user_id WHERE f.follower_id = ? AND u.username = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, App.currentUser.getId());
            ps.setString(2, targetUsername);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public void tambahTeman(String targetUsername) {
        String getTargetId = "SELECT user_id FROM users WHERE username = ?";
        String insertFollow = "INSERT INTO follows (follower_id, followed_id) VALUES (?, ?)";
        try (Connection conn = Koneksi.getKoneksi()) {
            int targetId = -1;
            PreparedStatement ps1 = conn.prepareStatement(getTargetId);
            ps1.setString(1, targetUsername);
            ResultSet rs = ps1.executeQuery();
            if(rs.next()) targetId = rs.getInt("user_id");
            if(targetId != -1) {
                PreparedStatement ps2 = conn.prepareStatement(insertFollow);
                ps2.setInt(1, App.currentUser.getId());
                ps2.setInt(2, targetId);
                ps2.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void hapusTeman(String targetUsername) {
        String query = "DELETE f FROM follows f JOIN users u ON f.followed_id = u.user_id WHERE f.follower_id = ? AND u.username = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, App.currentUser.getId());
            ps.setString(2, targetUsername);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public int getFollowerCount(String username) {
        String query = "SELECT COUNT(*) FROM follows f JOIN users u ON f.followed_id = u.user_id WHERE u.username = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    public int getFollowingCount(String username) {
        String query = "SELECT COUNT(*) FROM follows f JOIN users u ON f.follower_id = u.user_id WHERE u.username = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
}