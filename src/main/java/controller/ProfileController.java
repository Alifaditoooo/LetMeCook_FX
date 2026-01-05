package controller;

import cooked.App;
import cooked.LayananResep;
import cooked.Resep;
import cooked.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

    private static final String FOLDER_GAMBAR = "C:\\CookedUploads";

    public void initialize() {
        buatFolderJikaBelumAda();
    }

    public void setTargetUsername(String username) {
        this.targetUsername = username;
        try {
            buatFolderJikaBelumAda();
            initDesign();
            refreshProfile();
        } catch (Exception e) {
            System.out.println("Error saat memuat profil: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void buatFolderJikaBelumAda() {
        try {
            File folder = new File(FOLDER_GAMBAR);
            if (!folder.exists()) {
                folder.mkdirs(); 
            }
        } catch (Exception e) {}
    }
    
    private void initDesign() {
        if(imgProfile != null) {
            imgProfile.setPreserveRatio(false); 
            double radius = 60; 
            imgProfile.setFitWidth(radius * 2);
            imgProfile.setFitHeight(radius * 2);
            
            Circle clip = new Circle(radius, radius, radius); 
            imgProfile.setClip(clip);
        }
    }
    
    private void refreshProfile() {
        if (lblUsernameHeader != null) lblUsernameHeader.setText(targetUsername);
        
        int followers = layanan.getFollowerCount(targetUsername);
        int following = layanan.getFollowingCount(targetUsername);
        
        if(lblFollowers != null) lblFollowers.setText(String.valueOf(followers));
        if(lblFollowing != null) lblFollowing.setText(String.valueOf(following));
        
        loadProfileImage();

        String myUsername = layanan.getCurrentUsername();
        User currentUser = layanan.getCurrentUser();
        
        if (currentUser != null && targetUsername.equals(myUsername)) {
            if(btnEditFoto != null) {
                btnEditFoto.setVisible(true);
                btnEditFoto.setOnAction(e -> handleUploadFoto());
            }

            if(btnAction != null) {
                btnAction.setText("LOGOUT");
                btnAction.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
                btnAction.setOnAction(e -> handleLogout());
            }
            
        } else {
            if(btnEditFoto != null) btnEditFoto.setVisible(false);
            
            if(btnAction != null) {
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
                    if(lblFollowers != null) lblFollowers.setText(String.valueOf(layanan.getFollowerCount(targetUsername)));
                });
            }
        }
        
        loadResepUser();
    }
    
    private void loadProfileImage() {
        try {
            File fileGambar = new File(FOLDER_GAMBAR + "\\profile_" + targetUsername + ".jpg");
            if (fileGambar.exists()) {
                if (fileGambar.length() == 0) {
                    System.gc();
                    fileGambar.delete();
                    imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
                    return;
                }
                try (FileInputStream input = new FileInputStream(fileGambar)) {
                     Image image = new Image(input);
                     imgProfile.setImage(image);
                }
            } else {
                 imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {
            try {
                imgProfile.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            } catch (Exception ex) {}
        }
    }

    private void handleUploadFoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Foto Profil");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Gambar", "*.jpg", "*.png", "*.jpeg"));
        File fileSumber = fc.showOpenDialog(imgProfile.getScene().getWindow());
        if (fileSumber != null) {
            try {
                File fileTujuan = new File(FOLDER_GAMBAR, "profile_" + targetUsername + ".jpg");
                if (fileTujuan.exists()) {
                    System.gc(); 
                    fileTujuan.delete(); 
                }
                Files.copy(fileSumber.toPath(), fileTujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
                loadProfileImage();
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
    }
    
    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            btnAction.setText("Mengikuti");
            btnAction.setStyle("-fx-background-color: white; -fx-text-fill: #FF451F; -fx-border-color: #FF451F; -fx-border-radius: 20; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            btnAction.setText("Ikuti");
            btnAction.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");
        }
    }
    
    private void loadResepUser() {
        if(gridResep == null) return;
        gridResep.getChildren().clear();
        List<Resep> resepUser = layanan.getResepByUsername(targetUsername);
        if (resepUser.isEmpty()) {
            Label empty = new Label("Belum ada resep.");
            empty.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
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
                File f = new File(FOLDER_GAMBAR + "\\" + resep.getGambarFilename());
                if(f.exists() && f.length() > 0) {
                    try (FileInputStream fis = new FileInputStream(f)) {
                        imgView.setImage(new Image(fis));
                    }
                } else {
                     imgView.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
                }
            } else {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_baru.png").toExternalForm()));
            }
        } catch (Exception e) {}

        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setWrapText(true);
        lblJudul.setPrefWidth(180);
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333; -fx-padding: 0 10 0 10;");

        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(0, 10, 0, 10));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblLike = new Label("❤ " + resep.getJumlahLike());
        lblLike.setStyle("-fx-text-fill: #FF451F; -fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        bottomBox.getChildren().addAll(lblLike, spacer);
        
        if (layanan.getCurrentUser() != null && layanan.getCurrentUser().getId() == resep.getPenulisId()) {
            Button btnHapus = new Button("🗑");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-padding: 0; -fx-cursor: hand; -fx-font-size: 14px;");
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
        } catch (IOException e) { 
            e.printStackTrace(); 
            System.out.println("Gagal memuat halaman Home.");
        }
    }
}