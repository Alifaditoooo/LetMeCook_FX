package controller;

import cooked.LayananResep;
import cooked.Resep;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ProfileController {

    @FXML private ImageView imgProfile;
    @FXML private Label lblUsernameHeader;
    @FXML private Label lblFollowers;
    @FXML private Label lblFollowing;
    @FXML private Button btnAction;
    @FXML private FlowPane gridResep;
    
    private LayananResep layanan = new LayananResep();
    private String targetUsername;

    public void setTargetUsername(String username) {
        this.targetUsername = username;
        initDesign();
        refreshProfile();
    }
    
    private void initDesign() {
        if(imgProfile != null) {
            double radius = imgProfile.getFitWidth() / 2;
            Circle clip = new Circle(radius, radius, radius);
            imgProfile.setClip(clip);
        }
    }
    
    private void refreshProfile() {
        lblUsernameHeader.setText(targetUsername);
        
        int followers = layanan.getFollowerCount(targetUsername);
        int following = layanan.getFollowingCount(targetUsername);
        
        lblFollowers.setText(String.valueOf(followers));
        lblFollowing.setText(String.valueOf(following));
        
        String myUsername = layanan.getCurrentUsername();
        
        if (targetUsername.equals(myUsername)) {
            btnAction.setText("LOGOUT");
            btnAction.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 20;");
            btnAction.setOnAction(e -> handleLogout());
        } else {
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
    
    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            btnAction.setText("Mengikuti (Unfollow)");
            btnAction.setStyle("-fx-background-color: white; -fx-text-fill: #FF451F; -fx-border-color: #FF451F; -fx-border-radius: 20; -fx-background-radius: 20;");
        } else {
            btnAction.setText("Ikuti (Follow)");
            btnAction.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-background-radius: 20;");
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
                imgView.setImage(new Image("file:C:\\CookedUploads\\" + resep.getGambarFilename()));
            } else {
                imgView.setImage(new Image(getClass().getResource("/cooked/bg_home.png").toExternalForm()));
            }
        } catch (Exception e) {}

        Label lblJudul = new Label(resep.getJudul());
        lblJudul.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 10;");
        
        javafx.scene.layout.HBox bottomBox = new javafx.scene.layout.HBox(10);
        bottomBox.setPadding(new Insets(0, 10, 0, 10));
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblLike = new Label("❤ " + resep.getJumlahLike());
        lblLike.setStyle("-fx-text-fill: #FF451F; -fx-font-size: 12px;");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        bottomBox.getChildren().addAll(lblLike, spacer);

        if (layanan.getCurrentUser() != null && layanan.getCurrentUser().getId() == resep.getPenulisId()) {
            Button btnHapus = new Button("🗑");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-padding: 0;");
            
            btnHapus.setOnAction(e -> {
                e.consume();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Hapus Resep");
                alert.setHeaderText("Hapus resep ini?");
                alert.setContentText(resep.getJudul());
                
                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        layanan.hapusResep(resep.getId());
                        refreshProfile();
                    }
                });
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
            controller.ResepDetailController detailController = loader.getController();
            detailController.setResep(resep);
            Stage stage = (Stage) gridResep.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBack() {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/login_form.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridResep.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}