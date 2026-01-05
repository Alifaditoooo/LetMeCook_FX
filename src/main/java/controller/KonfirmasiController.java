package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class KonfirmasiController {

    @FXML private Label lblJudul;
    @FXML private Label lblPesan;
    
    private Runnable aksiYa; 

    public void setKonten(String judul, String pesan, Runnable aksiYa) {
        this.lblJudul.setText(judul);
        this.lblPesan.setText(pesan);
        this.aksiYa = aksiYa;
    }

    @FXML
    private void handleYa() {
        if (aksiYa != null) {
            aksiYa.run(); 
        }
        closeDialog();
    }

    @FXML
    private void handleBatal() {
        closeDialog(); 
    }

    private void closeDialog() {
        Stage stage = (Stage) lblJudul.getScene().getWindow();
        stage.close();
    }
}