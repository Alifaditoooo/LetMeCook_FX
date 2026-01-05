package controller;

import cooked.App;
import cooked.LayananResep;
import cooked.Resep;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    @FXML private Button btnKembali;

    private LayananResep layanan = new LayananResep();

    public void initialize() {
        if (App.resepDetail != null) {
            tampilkanData(App.resepDetail);
            cekStatusLike();
        }
    }
    
    public void setResep(Resep resep) {
        App.resepDetail = resep; 
        tampilkanData(resep);      
        cekStatusLike();
    }

    private void tampilkanData(Resep resep) {
        if (resep != null) {
            lblJudulHeader.setText(resep.getJudul());
            
            String penulis = (resep.getPenulis() != null) ? resep.getPenulis() : "Chef User";
            lblPenulis.setText("by " + penulis);
            
            lblDeskripsi.setText(resep.getDeskripsi());
            lblBahan.setText(resep.getBahan());
            lblLangkah.setText(resep.getLangkah());
            lblLikeCount.setText("❤ " + resep.getJumlahLike() + " Like");
            
            try {
                if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                    String path = "C:\\CookedUploads\\" + resep.getGambarFilename();
                    File fileGambar = new File(path);
                    if (fileGambar.exists()) {
                        imgResepHeader.setImage(new Image(fileGambar.toURI().toString()));
                    } else {
                        imgResepHeader.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error load gambar: " + e.getMessage());
            }
        }
    }

    private void cekStatusLike() {
        if (App.resepDetail != null && App.currentUser != null) {
            boolean sudahLike = layanan.isResepDisukai(App.resepDetail.getId(), App.currentUser.getId());
            if (sudahLike) {
                btnLike.setText("SUDAH DISUKAI ❤");
                btnLike.setDisable(true);
                btnLike.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #FF451F;");
            }
        }
    }

    @FXML
    private void handleLike(ActionEvent event) {
        if (App.resepDetail != null && App.currentUser != null) {
            boolean sukses = layanan.toggleLike(App.resepDetail.getId(), App.currentUser.getId());
            
            if (sukses) {
                int currentLike = App.resepDetail.getJumlahLike();
                App.resepDetail.setJumlahLike(currentLike + 1);
                
                lblLikeCount.setText("❤ " + App.resepDetail.getJumlahLike() + " Like");
                
                btnLike.setText("SUDAH DISUKAI ❤");
                btnLike.setDisable(true);
            }
        }
    }

    @FXML
    private void handleProfil(MouseEvent event) {
        if (App.resepDetail != null) {
            System.out.println("Ke profil ID: " + App.resepDetail.getPenulisId());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/cooked/beranda.fxml"));
            Stage stage = (Stage) lblJudulHeader.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}