package controller;

import cooked.LayananResep;
import cooked.Resep;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProfileController {

    @FXML private ImageView imgProfile;
    @FXML private Label lblUsernameHeader;
    @FXML private Label lblFollowers;
    @FXML private Label lblFollowing;
    @FXML private Button btnAction;
    @FXML private Button btnEditFoto;
    @FXML private FlowPane gridResep;
    
    private LayananResep layanan = new LayananResep();
    private String targetUsername;

    // FOLDER KHUSUS UNTUK MENAMPUNG GAMBAR
    private static final String FOLDER_GAMBAR = "C:\\CookedUploads";

    public void setTargetUsername(String username) {
        this.targetUsername = username;
        initDesign();
        refreshProfile();
    }
    
    private void initDesign() {
        if(imgProfile != null) {
            // Membuat foto jadi bulat
            Circle clip = new Circle(60, 60, 60); 
            imgProfile.setClip(clip);
        }
    }
    
    private void refreshProfile() {
        lblUsernameHeader.setText(targetUsername);
        
        int followers = layanan.getFollowerCount(targetUsername);
        int following = layanan.getFollowingCount(targetUsername);
        
        lblFollowers.setText(String.valueOf(followers));
        lblFollowing.setText(String.valueOf(following));
        
        loadProfileImage();

        String myUsername = layanan.getCurrentUsername();
        
        if (targetUsername.equals(myUsername)) {
            // PROFIL SENDIRI: Tampilkan tombol Ganti Foto
            if(btnEditFoto != null) btnEditFoto.setVisible(true);
            
            btnAction.setText("LOGOUT");
            btnAction.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
            btnAction.setOnAction(e -> handleLogout());
        } else {
            // PROFIL ORANG LAIN: Sembunyikan tombol Ganti Foto
            if(btnEditFoto != null) btnEditFoto.setVisible(false);
            
            boolean isFollowing = layanan.isTeman(targetUsername);
            updateFollowButton(isFollowing);
            
            btnAction.setOnAction(e -> {
                if (layanan.isTeman(targetUsername)) {
                    layanan.hapusTeman(targetUsername);
                    updateFollowButton(false);
                } else {
                    layanan.tambahTeman(targetUsername);
                    updateFollowButton(true);
                }
                lblFollowers.setText(String.valueOf(layanan.getFollowerCount(targetUsername)));
            });
        }
        
        loadResepUser();
    }
    
    private void loadProfileImage() {
        try {
            // Cek apakah ada foto di folder khusus
            File fileGambar = new File(FOLDER_GAMBAR + "\\profile_" + targetUsername + ".jpg");
            
            if (fileGambar.exists()) {
                 // Pakai timestamp biar gambar langsung keganti (refresh cache)
                 imgProfile.setImage(new Image(fileGambar.toURI().toString() + "?" + System.currentTimeMillis()));
            } else {
                 // Gambar default jika file tidak ada
                 imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {
            System.out.println("Gagal load gambar: " + e.getMessage());
        }
    }

    // --- LOGIKA UPLOAD KE FOLDER KHUSUS ---
    @FXML
    private void handleUploadFoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Foto Profil");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Gambar", "*.jpg", "*.png", "*.jpeg"));
        
        File fileSumber = fc.showOpenDialog(imgProfile.getScene().getWindow());
        
        if (fileSumber != null) {
            try {
                // 1. BUAT FOLDER JIKA BELUM ADA
                File folder = new File(FOLDER_GAMBAR);
                if (!folder.exists()) {
                    folder.mkdirs(); 
                }
                
                // 2. COPY FILE KE FOLDER ITU
                // Nama file diseragamkan: profile_USERNAME.jpg
                File fileTujuan = new File(folder, "profile_" + targetUsername + ".jpg");
                
                Files.copy(fileSumber.toPath(), fileTujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // 3. TAMPILKAN HASILNYA
                Image newImg = new Image(fileTujuan.toURI().toString() + "?" + System.currentTimeMillis());
                imgProfile.setImage(newImg);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Berhasil");
                alert.setContentText("Foto profil berhasil disimpan di " + FOLDER_GAMBAR);
                alert.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Gagal");
                alert.setContentText("Gagal upload: " + e.getMessage());
                alert.show();
            }
        }
    }
    
  
    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            btnAction.setText("Mengikuti (Unfollow)");
            btnAction.setStyle("-fx-background-color: white; -fx-text-fill: #FF451F; -fx-border-color: #FF451F; -fx-border-radius: 20; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            btnAction.setText("Ikuti (Follow)");
            btnAction.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
        }
    }
    
    private void loadResepUser() {
        gridResep.getChildren().clear();
        List<Resep> resepUser = layanan.getResepByUsername(targetUsername);
        if (resepUser.isEmpty()) {
            Label empty = new Label("Belum ada resep yang diupload.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            gridResep.getChildren().add(empty);
        } else {
            for (Resep r : resepUser) {
                gridResep.getChildren().add(buatKartuResep(r));
            }
        }
    }
    
    private VBox buatKartuResep(Resep resep) {
        VBox kartu = new VBox(5);
        kartu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-cursor: hand;");
        kartu.setPrefWidth(200);
        kartu.setPadding(new Insets(0,0,10,0));
        kartu.setOnMouseClicked(e -> bukaDetailResep(resep));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(200);
        imgView.setFitHeight(130);

        try {
            if (resep.getGambarFilename() != null && !resep.getGambarFilename().isEmpty()) {
                imgView.setImage(new Image("file:" + FOLDER_GAMBAR + "\\" + resep.getGambarFilename()));
            } else {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {}

        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 10;");
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(0, 10, 0, 10));
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label lblLike = new Label("❤ " + resep.getJumlahLike());
        lblLike.setStyle("-fx-text-fill: #FF451F; -fx-font-size: 12px;");
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        bottomBox.getChildren().addAll(lblLike, spacer);
        
        if (layanan.getCurrentUser() != null && layanan.getCurrentUser().getId() == resep.getPenulisId()) {
            Button btnHapus = new Button("🗑");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-padding: 0;");
            btnHapus.setOnAction(e -> {
                e.consume();
                layanan.hapusResep(resep.getId());
                refreshProfile();
            });
            bottomBox.getChildren().add(btnHapus);
        }
        kartu.getChildren().addAll(imgView, lblJudul, bottomBox);
        return kartu;
    }

    private void bukaDetailResep(Resep resep) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/resep_detail.fxml"));
            Parent root = loader.load();
            ResepDetailController detailController = loader.getController();
            detailController.setResep(resep);
            Stage stage = (Stage) gridResep.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridResep.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void handleLogout() {
        layanan.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridResep.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}