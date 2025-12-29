package controller;

import cooked.LayananResep;
import cooked.Resep;
import cooked.User;
import java.io.File;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane; // IMPORT PENTING
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class BerandaController {

    @FXML private FlowPane feedContainer; 
    @FXML private Button btnLogout;
    @FXML private Label lblUsername;
    @FXML private TextField inputSearch; 
    
    private LayananResep layanan = new LayananResep();
    private String currentTab = "ALL"; 
    
    private static final String FOLDER_GAMBAR = "C:\\CookedUploads";

    public void initialize() {
        if (lblUsername != null) {
            lblUsername.setText(layanan.getCurrentUsername()); 
            lblUsername.setOnMouseClicked(e -> bukaHalamanProfil(layanan.getCurrentUsername()));
            lblUsername.setStyle("-fx-cursor: hand;");
        }
        loadBeranda(); 
    }

    @FXML private void loadBeranda() {
        currentTab = "ALL";
        tampilkanList(layanan.getSemuaResep());
    }

    @FXML private void loadPopuler() {
        currentTab = "POPULER";
        tampilkanList(layanan.getResepPopuler());
    }
    
    @FXML private void loadTeman() {
        currentTab = "TEMAN";
        feedContainer.getChildren().clear();
        
        try {
            List<User> temanList = layanan.getDaftarTeman();
            
            if (temanList.isEmpty()) {
                Label info = new Label("Kamu belum mengikuti siapapun.\nCari teman di kolom pencarian!");
                info.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-text-alignment: center;");
                feedContainer.getChildren().add(info);
            } else {
                for (User teman : temanList) {
                    feedContainer.getChildren().add(buatKartuTeman(teman));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLbl = new Label("Gagal memuat teman.");
            feedContainer.getChildren().add(errorLbl);
        }
    }

    @FXML private void loadDisukai() {
        currentTab = "DISUKAI"; 
        tampilkanList(layanan.getResepDisukai()); 
    }

    @FXML private void handleSearch() {
        String keyword = inputSearch.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadBeranda();
        } else {
            currentTab = "SEARCH";
            tampilkanList(layanan.cariResep(keyword));
        }
    }

    @FXML private void filterRingan() { filterByKategori("Makanan Ringan"); }
    @FXML private void filterBerat() { filterByKategori("Makanan Berat"); }
    @FXML private void filterMinuman() { filterByKategori("Minuman"); }

    private void filterByKategori(String kategori) {
        currentTab = "FILTER_" + kategori;
        tampilkanList(layanan.filterKategori(kategori));
    }

    private void tampilkanList(List<Resep> listResep) {
        feedContainer.getChildren().clear(); 
        if (listResep.isEmpty()) {
            Label kosong = new Label("Tidak ada resep ditemukan...");
            kosong.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            feedContainer.getChildren().add(kosong);
        } else {
            for (Resep resep : listResep) {
                feedContainer.getChildren().add(buatKartuResep(resep));
            }
        }
    }

    // --- KARTU TEMAN (DENGAN JUMLAH FOLLOWER DI TENGAH) ---
    private VBox buatKartuTeman(User teman) {
        VBox kartu = new VBox(10);
        kartu.setAlignment(Pos.CENTER);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        kartu.setPrefSize(180, 220); // Sedikit dipertinggi agar muat
        kartu.setPadding(new Insets(20));

        // 1. StackPane untuk menumpuk Lingkaran & Angka
        StackPane stackProfile = new StackPane();
        
        // 2. Lingkaran Dasar
        Circle circlePlaceholder = new Circle(40); 
        circlePlaceholder.setFill(Color.web("#FFF8E7")); // Warna krem muda (agar teks terlihat jelas)
        circlePlaceholder.setStroke(Color.ORANGE); // Garis pinggir oranye
        circlePlaceholder.setStrokeWidth(2);

        // 3. Teks Jumlah Follower
        int count = layanan.getFollowerCount(teman.getUsername());
        Label lblCount = new Label(String.valueOf(count));
        lblCount.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: #FF451F;");
        
        Label lblLabelKecil = new Label("Followers");
        lblLabelKecil.setStyle("-fx-font-size: 9px; -fx-text-fill: #888;");
        lblLabelKecil.setTranslateY(15); // Geser teks kecil ke bawah sedikit

        // Gabungkan ke StackPane
        stackProfile.getChildren().addAll(circlePlaceholder, lblCount, lblLabelKecil);

        // 4. Nama User
        Label lblNama = new Label(teman.getUsername());
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");

        // 5. Tombol Lihat Profil
        Button btnProfile = new Button("Lihat Profil");
        btnProfile.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
        btnProfile.setOnAction(e -> bukaHalamanProfil(teman.getUsername()));
        
        // 6. Tombol Unfollow
        Button btnUnfollow = new Button("Unfollow");
        btnUnfollow.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-cursor: hand; -fx-font-size: 11px;");
        btnUnfollow.setOnAction(e -> {
            layanan.hapusTeman(teman.getUsername());
            loadTeman(); 
        });

        kartu.getChildren().addAll(stackProfile, lblNama, btnProfile, btnUnfollow);
        return kartu;
    }

    // --- KARTU RESEP (SAMA SEPERTI SEBELUMNYA) ---
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(0);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        kartu.setPrefWidth(220); 
        
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(150);
        imgView.setStyle("-fx-cursor: hand;"); 
        
        try {
            if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                imgView.setImage(new Image("file:" + FOLDER_GAMBAR + "\\" + resep.getGambarFilename()));
            } else {
                 imgView.setImage(null); 
            }
        } catch (Exception e) {}
        
        imgView.setOnMouseClicked(e -> bukaDetailResep(resep));

        Button btnJudul = new Button(resep.getJudul());
        btnJudul.setPrefWidth(220);
        btnJudul.setWrapText(true);
        btnJudul.setAlignment(Pos.CENTER_LEFT); 
        btnJudul.setStyle("-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 10 0 10;");
        btnJudul.setOnAction(e -> bukaDetailResep(resep));

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(5, 10, 10, 10)); 

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
        
        if (layanan.getCurrentUser() != null && layanan.getCurrentUser().getId() == resep.getPenulisId()) {
            Button btnHapus = new Button("🗑");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0;");
            btnHapus.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Hapus Resep");
                alert.setContentText("Hapus resep " + resep.getJudul() + "?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        layanan.hapusResep(resep.getId());
                        if (currentTab.equals("ALL")) loadBeranda();
                        else if (currentTab.equals("POPULER")) loadPopuler();
                        else if (currentTab.equals("DISUKAI")) loadDisukai();
                        else if (currentTab.startsWith("FILTER_")) filterByKategori(currentTab.substring(7));
                        else if (currentTab.equals("SEARCH")) handleSearch();
                    }
                });
            });
            actionBox.getChildren().addAll(btnLike, lblJmlLike, spacer, lblPenulis, btnHapus);
        } else {
            actionBox.getChildren().addAll(btnLike, lblJmlLike, spacer, lblPenulis);
        }

        kartu.getChildren().addAll(imgView, btnJudul, actionBox);
        return kartu;
    }

    private void bukaDetailResep(Resep resepDiklik) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/resep_detail.fxml"));
            Parent root = loader.load();
            ResepDetailController detailController = loader.getController();
            detailController.setResep(resepDiklik);
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void bukaHalamanProfil(String usernameTarget) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/profile.fxml"));
            Parent root = loader.load();
            ProfileController profileController = loader.getController();
            profileController.setTargetUsername(usernameTarget);
            Stage stage = (Stage) feedContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
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