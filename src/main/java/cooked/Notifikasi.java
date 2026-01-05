package cooked;

import controller.NotificationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Notifikasi {

    public static void tampilkan(String tipe, String judul, String pesan) {
        try {

            FXMLLoader loader = new FXMLLoader(Notifikasi.class.getResource("/cooked/notification.fxml"));
            Parent root = loader.load();

            NotificationController controller = loader.getController();
            controller.setPesan(judul, pesan, tipe);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}