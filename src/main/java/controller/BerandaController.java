package controller;

import cooked.LayananResep;
import cooked.Notifikasi; 
import cooked.Konfirmasi; 
import cooked.Resep;
import cooked.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class BerandaController {

    @FXML private Label lblUsername;
    @FXML private FlowPane feedContainer;
    @FXML private TextField inputSearch;
    @FXML private Button btnUpload;
    @FXML private Button btnLogout;
    
    private LayananResep layanan = new LayananResep();
    
    // Lokasi folder gambar
    private static final String FOLDER_GAMBAR = "C:\\CookedUploads";

    public void initialize() {
        User user = layanan.getCurrentUser();
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblUsername.setCursor(Cursor.HAND);
            lblUsername.setOnMouseClicked(e -> {
                bukaProfilTeman(user.getUsername());
            });
        }
        loadBeranda(); 
    }

    // --- NAVIGASI UTAMA ---
    
    @FXML
    private void loadBeranda() { 
        feedContainer.getChildren().clear();
        List<Resep> list = layanan.getSemuaResep();
        renderList(list, "Belum ada resep yang diposting.");
    }
    
    @FXML 
    private void loadPopuler() {
        feedContainer.getChildren().clear();
        List<Resep> list = layanan.getResepPopuler();
        renderList(list, "Belum ada resep populer.");
    }
    
    @FXML 
    private void loadTeman() {
        feedContainer.getChildren().clear();
        List<User> listTeman = layanan.getDaftarFollowing();
        
        if (listTeman.isEmpty()) {
            tampilkanPesanKosong("Kamu belum mengikuti siapapun.");
        } else {
            for (User u : listTeman) {
                feedContainer.getChildren().add(buatKartuTeman(u));
            }
        }
    }
    
    @FXML 
    private void loadDisukai() {
        feedContainer.getChildren().clear();
        List<Resep> list = layanan.getResepDisukai();
        renderList(list, "Kamu belum menyukai resep apapun.");
    }

    // --- PENCARIAN & FILTER ---
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = inputSearch.getText().toLowerCase();
        feedContainer.getChildren().clear();
        
        List<Resep> hasil;
        if (keyword.isEmpty()) {
            hasil = layanan.getSemuaResep();
        } else {
            hasil = layanan.cariResep(keyword);
        }
        renderList(hasil, "Tidak ditemukan: " + keyword);
    }

    @FXML private void filterRingan() { filterKategori("Makanan Ringan"); }
    @FXML private void filterBerat() { filterKategori("Makanan Berat"); }
    @FXML private void filterMinuman() { filterKategori("Minuman"); }

    private void filterKategori(String kategori) {
        feedContainer.getChildren().clear();
        List<Resep> hasil = layanan.getResepByKategori(kategori);
        renderList(hasil, "Kategori " + kategori + " kosong.");
    }

    private void renderList(List<Resep> list, String pesanKosong) {
        if (list.isEmpty()) {
            tampilkanPesanKosong(pesanKosong);
        } else {
            for (Resep r : list) {
                feedContainer.getChildren().add(buatKartuResep(r));
            }
        }
    }

    // --- NAVIGASI KE UPLOAD ---
    @FXML
    private void bukaHalamanUpload(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/upload.fxml"));
            Parent root = loader.load();
            
            Stage stage;
            if (btnUpload != null) {
                stage = (Stage) btnUpload.getScene().getWindow();
            } else {
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            }
            
            stage.setScene(new Scene(root));
            
        } catch (Exception e) {
            e.printStackTrace();
            Notifikasi.tampilkan("ERROR", "Gagal Membuka Halaman", 
                "Terjadi error saat memuat halaman Upload:\n" + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        layanan.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/login_form.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- UI KARTU TEMAN ---
    private VBox buatKartuTeman(User u) {
        VBox kartu = new VBox(5); 
        kartu.setAlignment(Pos.CENTER);
        kartu.setPadding(new Insets(20));
        kartu.setPrefSize(180, 230); 
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        
        ImageView imgProfile = new ImageView();
        imgProfile.setFitWidth(80);
        imgProfile.setFitHeight(80);
        imgProfile.setPreserveRatio(false); 
        
        try {
            File fileGambar = new File(FOLDER_GAMBAR + "\\profile_" + u.getUsername() + ".jpg");
            if (fileGambar.exists()) {
                FileInputStream fis = new FileInputStream(fileGambar);
                imgProfile.setImage(new Image(fis));
                fis.close();
            } else {
                imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {
             try {
                imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
             } catch(Exception ex){}
        }

        Circle clip = new Circle(40, 40, 40);
        imgProfile.setClip(clip);
        
        StackPane photoContainer = new StackPane();
        Circle border = new Circle(42);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.ORANGE);
        border.setStrokeWidth(2);
        photoContainer.getChildren().addAll(border, imgProfile);

        Label lblName = new Label(u.getUsername());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: black;");
        VBox.setMargin(lblName, new Insets(5, 0, 0, 0));

        int followers = layanan.getFollowerCount(u.getUsername());
        Label lblFollowers = new Label(followers + " Pengikut");
        lblFollowers.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Button btnProfil = new Button("Lihat Profil");
        btnProfil.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 12px; -fx-cursor: hand;");
        btnProfil.setOnAction(e -> bukaProfilTeman(u.getUsername()));
        VBox.setMargin(btnProfil, new Insets(10, 0, 5, 0));

        Label lblUnfollow = new Label("Unfollow");
        lblUnfollow.setStyle("-fx-text-fill: #999; -fx-font-size: 11px; -fx-cursor: hand; -fx-underline: true;");
        lblUnfollow.setOnMouseClicked(e -> {
            layanan.hapusTeman(u.getUsername());
            loadTeman(); 
        });
        
        kartu.getChildren().addAll(photoContainer, lblName, lblFollowers, btnProfil, lblUnfollow);
        return kartu;
    }

    // --- UI KARTU RESEP ---
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(5);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-cursor: hand;");
        kartu.setPrefWidth(220);
        kartu.setPadding(new Insets(0,0,10,0));
        
        kartu.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/resep_detail.fxml"));
                Parent root = loader.load();
                ResepDetailController detailCtrl = loader.getController();
                detailCtrl.setResep(resep); 
                Stage stage = (Stage) feedContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        ImageView img = new ImageView();
        img.setFitWidth(220);
        img.setFitHeight(140);
        try {
            if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                img.setImage(new Image("file:C:\\CookedUploads\\" + resep.getGambarFilename()));
            } else {
                img.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {}

        Label judul = new Label(resep.getJudul());
        judul.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 10 0 10; -fx-text-fill: #333;");
        judul.setWrapText(true);

        HBox infoBox = new HBox();
        infoBox.setPadding(new Insets(0, 10, 0, 10));
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblLike = new Label("❤ " + resep.getJumlahLike());
        lblLike.setStyle("-fx-text-fill: #FF451F; -fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label ket = new Label("by " + (resep.getPenulis()!=null ? resep.getPenulis() : "User"));
        ket.setStyle("-fx-text-fill: #888; -fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;");
        ket.setOnMouseClicked(e -> {
            e.consume(); 
            bukaProfilTeman(resep.getPenulis());
        });
        
        infoBox.getChildren().addAll(lblLike, spacer, ket);
        
        User currentUser = layanan.getCurrentUser();
        // Cek apakah resep ini milik user yang sedang login
        if (currentUser != null && currentUser.getId() == resep.getPenulisId()) {
            Button btnHapus = new Button("🗑");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
            
            // --- REVISI DI SINI: MENGGUNAKAN KONFIRMASI CUSTOM ---
            btnHapus.setOnAction(e -> {
                e.consume(); 
                
                // Panggil Popup Konfirmasi Custom
                Konfirmasi.tampilkan(
                    "Hapus Resep?", 
                    "Yakin ingin menghapus resep '" + resep.getJudul() + "'? Data yang dihapus tidak bisa kembali.", 
                    () -> {
                        // Aksi jika tombol HAPUS (Ya) ditekan
                        layanan.hapusResep(resep.getId());
                        loadBeranda();
                        Notifikasi.tampilkan("SUCCESS", "Terhapus", "Resep berhasil dihapus.");
                    }
                );
            });
            // -----------------------------------------------------
            
            infoBox.getChildren().add(btnHapus);
        }
        
        kartu.getChildren().addAll(img, judul, infoBox);
        return kartu;
    }
    
    private void bukaProfilTeman(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/profile.fxml"));
            Parent root = loader.load();
            
            ProfileController profileCtrl = loader.getController();
            profileCtrl.setTargetUsername(username); 
            
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            Notifikasi.tampilkan("ERROR", "Gagal Membuka Profil", e.getMessage());
        }
    }

    private void tampilkanPesanKosong(String pesan) {
        Label lbl = new Label(pesan);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        feedContainer.getChildren().add(lbl);
    }
}