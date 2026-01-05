package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationController {

    @FXML private Label lblJudul;
    @FXML private Label lblPesan;
    @FXML private Button btnOk;

    public void setPesan(String judul, String pesan, String tipe) {
        lblJudul.setText(judul);
        lblPesan.setText(pesan);
        
       
        if (tipe.equalsIgnoreCase("SUCCESS")) {
            lblJudul.setStyle("-fx-text-fill: #2E7D32;"); 
            btnOk.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-weight: bold;");
            btnOk.setText("OKE");
        } else {
        
            lblJudul.setStyle("-fx-text-fill: #D32F2F;"); 
            btnOk.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-weight: bold;");
            btnOk.setText("COBA LAGI");
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }
}