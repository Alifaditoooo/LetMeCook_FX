package controller;

import cooked.LayananResep;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField regUser;
    @FXML private PasswordField regPw;
    @FXML private Button btnDaftar;

    private LayananResep layanan = new LayananResep();

    @FXML
    private void handleRegisterAction() {
        String username = regUser.getText();
        String password = regPw.getText();

        if (username.isEmpty() || password.isEmpty()) {
            infoBox("Data tidak boleh kosong!", "Error");
            return;
        }

        if (layanan.register(username, password)) {
            infoBox("Akun berhasil dibuat! Silakan Login.", "Sukses");
            try {
                // Kembali ke halaman Login setelah register
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/login_form.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnDaftar.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            infoBox("Username sudah digunakan!", "Gagal");
        }
    }

    private void infoBox(String infoMessage, String titleBar) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
}