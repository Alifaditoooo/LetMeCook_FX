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

public class LoginController {

    @FXML 
    private TextField insertUSER;
    
    @FXML 
    private PasswordField insertPW;
    
    @FXML 
    private Button LOGIN;
    
    // Pastikan file LayananResep.java ada di package 'cooked' dan public
    private LayananResep layanan = new LayananResep();

    @FXML
    private void handleLoginAction() {
        String username = insertUSER.getText();
        String password = insertPW.getText();

        // Cek Login
        if (layanan.login(username, password)) {
            // Jika Login Benar, Coba Pindah Halaman
            try {
                // Memuat file beranda.fxml
                // Pastikan ada tanda '/' di depan path
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
                Parent root = loader.load();
                
                // Ambil Stage (Jendela) saat ini dan ganti Scene-nya
                Stage stage = (Stage) LOGIN.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                
            } catch (IOException e) {
                // Jika Gagal Pindah, Tampilkan Error Jelas di Layar
                e.printStackTrace(); 
                tampilkanPesan("Error Sistem", "Gagal membuka Beranda:\n" + e.getMessage());
            }
        } else {
            // Jika Login Salah
            tampilkanPesan("Gagal", "Username atau Password salah!");
        }
    }

    // Method untuk menampilkan pesan pop-up
    private void tampilkanPesan(String judul, String isi) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(isi);
        alert.showAndWait();
    }
}