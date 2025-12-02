package cooked;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BerandaController {

    @FXML
    private VBox feedContainer;
    @FXML
    private Button btnLogout;

    private LayananResep layanan = new LayananResep();

    // Dijalankan otomatis saat layar dibuka
    public void initialize() {
        refreshFeed();
    }

    @FXML
    private void refreshFeed() {
        feedContainer.getChildren().clear(); // Bersihkan feed lama
        
        List<Resep> semuaResep = layanan.getSemuaResep();
        
        if (semuaResep.isEmpty()) {
            feedContainer.getChildren().add(new Label("Belum ada resep. Jadilah yang pertama upload!"));
        } else {
            for (Resep resep : semuaResep) {
                feedContainer.getChildren().add(buatKartuResep(resep));
            }
        }
    }

    // Method untuk membuat Tampilan Kartu Resep secara manual (Coding UI)
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(10);
        kartu.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 15;");
        
        // 1. Judul
        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // 2. Penulis
        Label lblPenulis = new Label("Oleh: " + resep.getPenulisUsername());
        lblPenulis.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        // 3. Gambar (Jika ada)
        if (resep.getGambarFilename() != null) {
            try {
                // Mengambil gambar dari folder C:\CookedUploads\
                String pathLocal = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                Image img = new Image(pathLocal);
                ImageView imgView = new ImageView(img);
                imgView.setFitHeight(200);
                imgView.setPreserveRatio(true);
                kartu.getChildren().add(imgView);
            } catch (Exception e) {
                kartu.getChildren().add(new Label("[Gagal memuat gambar]"));
            }
        }

        // 4. Deskripsi
        Label lblDesk = new Label(resep.getDeskripsi());
        lblDesk.setWrapText(true);

        kartu.getChildren().addAll(lblJudul, lblPenulis, lblDesk);
        return kartu;
    }

    @FXML
    private void bukaHalamanUpload() {
        try {
            // Membuka window baru (pop-up) untuk upload
            FXMLLoader loader = new FXMLLoader(getClass().getResource("upload.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Upload Resep Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block window belakangnya
            stage.showAndWait(); // Tunggu sampai ditutup
            
            // Setelah ditutup, refresh feed agar resep baru muncul
            refreshFeed();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            layanan.logout();
            // Kembali ke Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}