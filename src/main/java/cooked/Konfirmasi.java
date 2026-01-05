package cooked;

import controller.KonfirmasiController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Konfirmasi {
    
    public static void tampilkan(String judul, String pesan, Runnable aksiJikaYa) {
        try {
            FXMLLoader loader = new FXMLLoader(Konfirmasi.class.getResource("/cooked/confirmation.fxml"));
            Parent root = loader.load();
            
            KonfirmasiController controller = loader.getController();
            controller.setKonten(judul, pesan, aksiJikaYa);
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); 
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}