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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BerandaController {

    @FXML private FlowPane feedContainer; 
    @FXML private Button btnLogout;
    @FXML private Label lblUsername;
    
    private LayananResep layanan = new LayananResep();
    private String currentTab = "ALL"; 

    public void initialize() {
        lblUsername.setText("Chef " + layanan.getCurrentUsername()); 
        loadBeranda(); // Default load semua resep
    }

    // --- A. NAVIGASI TAB ---

    @FXML
    private void loadBeranda() {
        currentTab = "ALL";
        System.out.println("Tab: For You");
        tampilkanList(layanan.getSemuaResep());
    }

    @FXML
    private void loadPopuler() {
        currentTab = "POPULER";
        System.out.println("Tab: Populer");
        tampilkanList(layanan.getResepPopuler());
    }
    
    @FXML
    private void loadTeman() {
        currentTab = "TEMAN";
        int jumlahMengikuti = layanan.getJumlahTeman();
        System.out.println("Tab: Teman (Mengikuti " + jumlahMengikuti + ")");
        
        List<Resep> resepTeman = layanan.getResepTeman();
        
        // Jika belum follow siapa-siapa atau teman belum posting (Sesuai PDF 2)
        if (resepTeman.isEmpty()) {
            feedContainer.getChildren().clear();
            
            VBox infoBox = new VBox(10);
            infoBox.setAlignment(Pos.CENTER);
            
            Label info = new Label("Kamu mengikuti " + jumlahMengikuti + " chef.");
            info.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #555;");
            
            Label subInfo = new Label("Belum ada resep baru di sini.\nCari teman di 'For You' dan klik nama mereka untuk Follow!");
            subInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-text-alignment: center;");
            
            infoBox.getChildren().addAll(info, subInfo);
            feedContainer.getChildren().add(infoBox);
        } else {
            tampilkanList(resepTeman);
        }
    }

    @FXML
    private void loadDisukai() {
        currentTab = "DISUKAI"; 
        System.out.println("Tab: Disukai");
        tampilkanList(layanan.getResepDisukai()); 
    }

    // --- B. RENDER TAMPILAN KARTU RESEP ---

    private void tampilkanList(List<Resep> listResep) {
        feedContainer.getChildren().clear(); 
        
        if (listResep.isEmpty() && !currentTab.equals("TEMAN")) {
            Label kosong = new Label("Tidak ada resep...");
            kosong.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20;");
            feedContainer.getChildren().add(kosong);
        } else if (!listResep.isEmpty()) {
            for (Resep resep : listResep) {
                feedContainer.getChildren().add(buatKartuResep(resep));
            }
        }
    }

    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(5);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        kartu.setPrefWidth(220); 
        kartu.setPadding(new Insets(0, 0, 10, 0)); 

        // 1. GAMBAR (Klik -> Detail)
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(150);
        imgView.setStyle("-fx-cursor: hand;");
        imgView.setOnMouseClicked(e -> bukaDetailResep(resep));

        try {
            if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                String pathLocal = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                imgView.setImage(new Image(pathLocal));
            } else {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_home.png").toExternalForm()));
            }
        } catch (Exception e) {}

        // 2. JUDUL
        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblJudul.setWrapText(true);
        lblJudul.setPadding(new Insets(5, 10, 0, 10));

        // 3. ACTION BAR (Like & Penulis)
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(5, 10, 5, 10));

        // TOMBOL LIKE (Fitur Inti)
        Button btnLike = new Button(resep.isDisukaiOlehSaya() ? "❤" : "♡");
        btnLike.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF451F; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;");
        
        Label lblJmlLike = new Label(String.valueOf(resep.getJumlahLike()));
        lblJmlLike.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Logic Klik Like
        btnLike.setOnAction(e -> {
            layanan.toggleLike(resep); 
            // Update Tampilan Langsung
            btnLike.setText(resep.isDisukaiOlehSaya() ? "❤" : "♡");
            lblJmlLike.setText(String.valueOf(resep.getJumlahLike()));
            
            // Jika sedang di tab Disukai, refresh agar resep yg di-unlike hilang
            if (currentTab.equals("DISUKAI")) loadDisukai();
        });

        // NAMA PENULIS (Klik -> Follow)
        Label lblPenulis = new Label("by " + resep.getPenulisUsername());
        lblPenulis.setStyle("-fx-text-fill: #555; -fx-font-size: 11px; -fx-cursor: hand; -fx-underline: true;");
        lblPenulis.setOnMouseClicked(e -> showProfilChef(resep.getPenulisUsername()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actionBox.getChildren().addAll(btnLike, lblJmlLike, spacer, lblPenulis);
        kartu.getChildren().addAll(imgView, lblJudul, actionBox);
        return kartu;
    }

    // --- C. POP-UP FOLLOW CHEF ---
    private void showProfilChef(String usernameChef) {
        // Jangan follow diri sendiri
        if (usernameChef.equals(layanan.getCurrentUsername())) return; 

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Profil Chef");
        alert.setHeaderText(usernameChef);
        
        boolean isFriend = layanan.isTeman(usernameChef);
        String status = isFriend ? "Status: Mengikuti" : "Status: Belum Mengikuti";
        alert.setContentText(status + "\nApakah kamu ingin mengikuti chef ini?");

        ButtonType btnFollow = new ButtonType(isFriend ? "Tutup" : "Ikuti (Follow)");
        ButtonType btnCancel = new ButtonType("Batal", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(btnFollow, btnCancel);

        alert.showAndWait().ifPresent(type -> {
            if (type == btnFollow && !isFriend) {
                layanan.tambahTeman(usernameChef);
                // Jika sedang di tab teman, refresh biar resep barunya muncul
                if (currentTab.equals("TEMAN")) loadTeman();
            }
        });
    }

    // --- D. NAVIGASI LAINNYA ---

    private void bukaDetailResep(Resep resepDiklik) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/resep_detail.fxml"));
            Parent root = loader.load();
            ResepDetailController detailController = loader.getController();
            detailController.setResep(resepDiklik);
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