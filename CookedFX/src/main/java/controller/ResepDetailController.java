package controller;

import cooked.Resep;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ResepDetailController {

    @FXML private ImageView imgResepHeader;
    @FXML private Label lblJudulHeader;
    @FXML private Label lblPenulis;
    @FXML private Label lblLikeCount;
    @FXML private Label lblDeskripsi;
    @FXML private Label lblBahan;
    @FXML private Label lblLangkah;
    @FXML private Button btnLike;

    private Resep currentResep;

    // Method ini dipanggil dari Beranda untuk menerima data resep
    public void setResep(Resep resep) {
        this.currentResep = resep;
        
        lblJudulHeader.setText(resep.getJudul());
        lblPenulis.setText(resep.getPenulisUsername());
        lblDeskripsi.setText(resep.getDeskripsi());
        
        // Cek agar tidak error jika data kosong
        lblBahan.setText(resep.getBahan() != null ? resep.getBahan() : "- Tidak ada data bahan");
        lblLangkah.setText(resep.getLangkah() != null ? resep.getLangkah() : "- Tidak ada data langkah");

        // Load Gambar
        if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
            try {
                String path = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                imgResepHeader.setImage(new Image(path));
            } catch (Exception e) {
                // Jika gagal load gambar, biarkan default atau kosong
            }
        }
        
        updateLikeStatus();
        
        // Aksi Tombol Like
        btnLike.setOnAction(e -> {
            if (currentResep.isDisukaiOlehSaya()) {
                currentResep.setDisukaiOlehSaya(false);
                currentResep.setJumlahLike(currentResep.getJumlahLike() - 1);
            } else {
                currentResep.setDisukaiOlehSaya(true);
                currentResep.tambahLike();
            }
            updateLikeStatus();
        });
    }
    
    private void updateLikeStatus() {
        lblLikeCount.setText("❤ " + currentResep.getJumlahLike() + " Like");
        if (currentResep.isDisukaiOlehSaya()) {
            btnLike.setText("UNLIKE 💔");
            btnLike.setStyle("-fx-background-color: white; -fx-text-fill: #FF5722; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            btnLike.setText("LIKE ❤");
            btnLike.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblJudulHeader.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}