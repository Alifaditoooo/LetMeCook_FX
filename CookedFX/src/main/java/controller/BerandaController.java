package controller;

import cooked.LayananResep;
import cooked.Resep;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane; // PENTING: Import Baru
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BerandaController {

    @FXML private FlowPane feedContainer; 
    @FXML private Button btnLogout;
    @FXML private Label lblUsername;
    
    private LayananResep layanan = new LayananResep();
    private String currentTab = "ALL"; 

    public void initialize() {
        lblUsername.setText(layanan.getCurrentUsername()); 
        // Klik nama sendiri -> Buka Profil
        lblUsername.setOnMouseClicked(e -> bukaHalamanProfil(layanan.getCurrentUsername()));
        lblUsername.setStyle("-fx-cursor: hand;");
        
        loadBeranda(); 
    }

    @FXML
    private void loadBeranda() {
        currentTab = "ALL";
        tampilkanList(layanan.getSemuaResep());
    }

    @FXML
    private void loadPopuler() {
        currentTab = "POPULER";
        tampilkanList(layanan.getResepPopuler());
    }
    
    @FXML
    private void loadTeman() {
        currentTab = "TEMAN";
        List<Resep> resepTeman = layanan.getResepTeman();
        if (resepTeman.isEmpty()) {
            feedContainer.getChildren().clear();
            Label info = new Label("Belum ada resep dari teman.");
            info.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            feedContainer.getChildren().add(info);
        } else {
            tampilkanList(resepTeman);
        }
    }

    @FXML
    private void loadDisukai() {
        currentTab = "DISUKAI"; 
        tampilkanList(layanan.getResepDisukai()); 
    }

    private void tampilkanList(List<Resep> listResep) {
        feedContainer.getChildren().clear(); 
        if (listResep.isEmpty()) {
            Label kosong = new Label("Tidak ada resep...");
            kosong.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            feedContainer.getChildren().add(kosong);
        } else {
            for (Resep resep : listResep) {
                feedContainer.getChildren().add(buatKartuResep(resep));
            }
        }
    }

    // --- METODE PEMBUAT KARTU YANG BARU ---
   // --- METODE PEMBUAT KARTU (Tampilan Bersih Ala Profil) ---
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(0);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        kartu.setPrefWidth(220); 
        
        // 1. GAMBAR (Klik juga bisa buka detail)
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(150);
        imgView.setStyle("-fx-cursor: hand;"); // Ubah kursor jadi tangan
        try {
            if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                String path = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                imgView.setImage(new Image(path));
            } else {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_home.png").toExternalForm()));
            }
        } catch (Exception e) {}
        
        imgView.setOnMouseClicked(e -> bukaDetailResep(resep));

        // 2. JUDUL SEBAGAI TOMBOL (Di Bawah Gambar)
        // Kita tetap pakai Button agar area kliknya luas & pasti, tapi stylenya kita buat transparan.
        Button btnJudul = new Button(resep.getJudul());
        btnJudul.setPrefWidth(220);
        btnJudul.setWrapText(true);
        btnJudul.setAlignment(Pos.CENTER_LEFT); // Rata Kiri
        
        // STYLE: Background Transparan, Teks Hitam (Mirip Profil)
        btnJudul.setStyle("-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 10 0 10;");
        
        // Aksi Klik Tombol
        btnJudul.setOnAction(e -> bukaDetailResep(resep));

        // 3. ACTION BAR (Like & Penulis)
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(5, 10, 10, 10)); // Padding dirapikan

        Button btnLike = new Button(resep.isDisukaiOlehSaya() ? "❤" : "♡");
        btnLike.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF451F; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;");
        Label lblJmlLike = new Label(String.valueOf(resep.getJumlahLike()));
        lblJmlLike.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        
        btnLike.setOnAction(e -> {
            layanan.toggleLike(resep); 
            btnLike.setText(resep.isDisukaiOlehSaya() ? "❤" : "♡");
            lblJmlLike.setText(String.valueOf(resep.getJumlahLike()));
            if (currentTab.equals("DISUKAI")) loadDisukai();
        });

        Label lblPenulis = new Label("by " + resep.getPenulisUsername());
        lblPenulis.setStyle("-fx-text-fill: #555; -fx-font-size: 11px; -fx-cursor: hand; -fx-underline: true;");
        lblPenulis.setOnMouseClicked(e -> bukaHalamanProfil(resep.getPenulisUsername()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actionBox.getChildren().addAll(btnLike, lblJmlLike, spacer, lblPenulis);

        // Gabungkan secara vertikal: Gambar -> Judul -> Info
        kartu.getChildren().addAll(imgView, btnJudul, actionBox);
        return kartu;
    } 
   private void bukaDetailResep(Resep resepDiklik) {
        try {
            System.out.println("Mencoba membuka detail resep: " + resepDiklik.getJudul());
            
            // Cek apakah file FXML ditemukan
            if (getClass().getResource("/cooked/resep_detail.fxml") == null) {
                throw new IOException("File resep_detail.fxml TIDAK DITEMUKAN di folder resources/cooked/");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/resep_detail.fxml"));
            Parent root = loader.load();
            
            ResepDetailController detailController = loader.getController();
            
            // Cek apakah Controller ditemukan
            if (detailController == null) {
                throw new IOException("Controller ResepDetailController GAGAL dimuat. Cek fx:controller di FXML.");
            }
            
            detailController.setResep(resepDiklik);
            
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            
        } catch (Exception e) { 
            e.printStackTrace(); // Print error merah di bawah (Output)
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Gagal Membuka Resep");
            alert.setHeaderText("Terjadi Kesalahan Sistem");
            alert.setContentText(e.getClass().getSimpleName() + ": " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void bukaHalamanProfil(String usernameTarget) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/profile.fxml"));
            Parent root = loader.load();
            ProfileController profileController = loader.getController();
            profileController.setTargetUsername(usernameTarget);
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void bukaHalamanUpload() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/upload.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout() {
        try {
            layanan.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/login_form.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}