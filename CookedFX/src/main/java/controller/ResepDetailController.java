package controller;

import cooked.LayananResep;
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
    private LayananResep layanan = new LayananResep();

    public void setResep(Resep resep) {
        this.currentResep = resep;
        
        // Safety Check: Pastikan elemen UI sudah dimuat sebelum diisi
        if (lblJudulHeader != null) lblJudulHeader.setText(resep.getJudul());
        if (lblPenulis != null) lblPenulis.setText(resep.getPenulisUsername());
        if (lblDeskripsi != null) lblDeskripsi.setText(resep.getDeskripsi());
        if (lblBahan != null) lblBahan.setText(resep.getBahan() != null ? resep.getBahan() : "-");
        if (lblLangkah != null) lblLangkah.setText(resep.getLangkah() != null ? resep.getLangkah() : "-");

        if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
            try {
                String path = "file:C:\\CookedUploads\\" + resep.getGambarFilename();
                if (imgResepHeader != null) imgResepHeader.setImage(new Image(path));
            } catch (Exception e) {}
        }
        
        // Panggil update UI
        updateLikeUI();
        
        // Pasang aksi tombol Like dengan Safety Check
        if (btnLike != null) {
            btnLike.setOnAction(e -> {
                layanan.toggleLike(currentResep); 
                updateLikeUI(); 
            });
        } else {
            System.out.println("PERINGATAN: btnLike masih NULL (mungkin belum ter-load)");
        }
    }
    
    private void updateLikeUI() {
        if (lblLikeCount != null) {
            lblLikeCount.setText("❤ " + currentResep.getJumlahLike() + " Like");
        }
        
        if (btnLike != null) {
            if (currentResep.isDisukaiOlehSaya()) {
                btnLike.setText("UNLIKE 💔");
                btnLike.setStyle("-fx-background-color: white; -fx-text-fill: #FF5722; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-color: #FF5722; -fx-border-width: 2;");
            } else {
                btnLike.setText("LIKE ❤");
                btnLike.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblJudulHeader.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}