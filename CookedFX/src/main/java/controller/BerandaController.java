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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BerandaController {

    @FXML
    private FlowPane feedContainer; // Menggunakan FlowPane untuk Grid Layout
    @FXML
    private Button btnLogout;
    @FXML
    private Label lblUsername;

    private LayananResep layanan = new LayananResep();

    public void initialize() {
        // Set nama user (bisa diambil dari session nanti)
        lblUsername.setText("Chef Kamu"); 
        refreshFeed();
    }

    @FXML
    private void refreshFeed() {
        feedContainer.getChildren().clear(); 
        
        List<Resep> semuaResep = layanan.getSemuaResep();
        
        if (semuaResep.isEmpty()) {
            feedContainer.getChildren().add(new Label("Belum ada resep. Jadilah yang pertama upload!"));
        } else {
            for (Resep resep : semuaResep) {
                feedContainer.getChildren().add(buatKartuResep(resep));
            }
        }
    }

    // Method Membuat Tampilan Kartu Resep ala PDF
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(5);
        // Style Kartu: Putih, Rounded, Shadow tipis
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        kartu.setPrefWidth(220); // Lebar fix agar rapi
        kartu.setPadding(new Insets(0, 0, 10, 0)); // Padding bawah saja

        // 1. GAMBAR (Bagian Atas)
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(150);
        
        // Membaca Gambar
        if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
            try {
                // Pastikan path ini sesuai dengan tempat kamu menyimpan upload
                String pathLocal = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                imgView.setImage(new Image(pathLocal));
            } catch (Exception e) {
                // Gambar Default jika gagal load
                 imgView.setImage(new Image(getClass().getResource("/cooked/bg_home.png").toExternalForm()));
            }
        } else {
             // Gambar Default jika null
             // Pastikan bg_home.png ada di resources/cooked
             try {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_home.png").toExternalForm()));
             } catch (Exception e) {}
        }
        
        // Agar gambar mengikuti lengkungan kartu atas
        // (Di JavaFX murni agak rumit clip-nya, jadi kita biarkan kotak dulu atau pakai RectangleClip)

        // 2. JUDUL & RATING
        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblJudul.setWrapText(true);
        
        Label lblRating = new Label("⭐ 4.9"); // Hardcode rating sesuai PDF dulu
        lblRating.setStyle("-fx-text-fill: #FF451F; -fx-font-size: 12px;");

        // 3. PENULIS
        Label lblPenulis = new Label(resep.getPenulisUsername());
        lblPenulis.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Container Teks
        VBox textBox = new VBox(3);
        textBox.setPadding(new Insets(5, 10, 5, 10));
        textBox.getChildren().addAll(lblJudul, lblPenulis, lblRating);

        // Gabungkan Gambar + Teks
        kartu.getChildren().addAll(imgView, textBox);
        
        return kartu;
    }

    @FXML
    private void bukaHalamanUpload() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/upload.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Upload Resep Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait(); 
            
            refreshFeed(); // Refresh setelah upload
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            layanan.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/login_form.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}