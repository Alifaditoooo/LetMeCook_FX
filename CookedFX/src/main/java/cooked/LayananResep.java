package cooked;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LayananResep {

    // --- DATABASE SEMENTARA (STATIC) ---
    // List Resep
    private static List<Resep> databaseResep = new ArrayList<>();
    // List Teman
    private static List<String> temanSaya = new ArrayList<>(); 
    // List User (UNTUK LOGIN & REGISTER) - INI YANG TADI HILANG
    private static List<User> databaseUser = new ArrayList<>();
    
    // Menyimpan siapa yang sedang login sekarang
    private static User currentUser;

    public LayananResep() {
        // Constructor kosong
        // Kita bisa isi satu user dummy buat ngetes login kalau mau
        if (databaseUser.isEmpty()) {
            User admin = new User();
            admin.setUsername("tes_user");
            admin.setPassword("12345");
            databaseUser.add(admin);
        }
    }

    // ==========================================
    // 1. BAGIAN AUTHENTICATION (LOGIN & REGISTER)
    // ==========================================

    public boolean register(String username, String password) {
        // Cek apakah username sudah dipakai orang lain
        for (User u : databaseUser) {
            if (u.getUsername().equals(username)) {
                return false; // Gagal, username sudah ada
            }
        }
        
        // Buat user baru
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        
        // Simpan ke database list
        databaseUser.add(newUser);
        return true; // Berhasil register
    }

    public boolean login(String username, String password) {
        // Cari user yang cocok
        for (User u : databaseUser) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                currentUser = u; // Set siapa yang login
                return true; // Login sukses
            }
        }
        return false; // Login gagal
    }
    
    public void logout() {
        currentUser = null;
    }
    
    // Method helper untuk mengambil username yang sedang login
    public String getCurrentUsername() {
        if (currentUser != null) {
            return currentUser.getUsername();
        }
        return "Chef Tamu"; // Default jika belum login
    }

    // ==========================================
    // 2. BAGIAN RESEP (FITUR UTAMA)
    // ==========================================

    public List<Resep> getSemuaResep() {
        return new ArrayList<>(databaseResep);
    }

    // Method Simpan Resep Baru
    public void tambahResepBaru(Resep resepBaru) {
        // Otomatis set penulis sesuai user yang sedang login
        if (currentUser != null) {
            resepBaru.setPenulisUsername(currentUser.getUsername());
        }
        // Tambah ke paling atas
        databaseResep.add(0, resepBaru);
    }

    public List<Resep> getResepPopuler() {
        List<Resep> salinan = new ArrayList<>(databaseResep);
        salinan.sort((r1, r2) -> Integer.compare(r2.getJumlahLike(), r1.getJumlahLike()));
        return salinan;
    }

    public List<Resep> getResepDisukai() {
        return databaseResep.stream()
                .filter(Resep::isDisukaiOlehSaya)
                .collect(Collectors.toList());
    }

    public List<Resep> getResepTeman() {
        return databaseResep.stream()
                .filter(resep -> temanSaya.contains(resep.getPenulisUsername()))
                .collect(Collectors.toList());
    }

    // --- FITUR LIKE & FOLLOW ---

    public void toggleLike(Resep resep) {
        if (resep.isDisukaiOlehSaya()) {
            resep.setDisukaiOlehSaya(false);
            resep.setJumlahLike(resep.getJumlahLike() - 1);
        } else {
            resep.setDisukaiOlehSaya(true);
            resep.tambahLike();
        }
    }

    public void tambahTeman(String usernameChef) {
        if (!temanSaya.contains(usernameChef)) {
            temanSaya.add(usernameChef);
        }
    }
    
    public boolean isTeman(String usernameChef) {
        return temanSaya.contains(usernameChef);
    }
}