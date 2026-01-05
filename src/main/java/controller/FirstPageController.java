package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class FirstPageController {

    @FXML private Button btnToLogin;
    @FXML private Button btnToRegister;

    @FXML
    private void handleToLogin() {
        pindahHalaman("/cooked/login_form.fxml");
    }

    @FXML
    private void handleToRegister() {
        pindahHalaman("/cooked/register.fxml");
    }

    private void pindahHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnToLogin.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}