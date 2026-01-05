package controller;

import cooked.LayananResep;
import cooked.Notifikasi; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    
    private LayananResep layanan = new LayananResep();

    @FXML
    private void handleLoginAction() {
        String username = insertUSER.getText();
        String password = insertPW.getText();

 
        if (layanan.login(username, password)) {
            
            try {                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) LOGIN.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                
            } catch (IOException e) {
                e.printStackTrace();            
                Notifikasi.tampilkan("ERROR", "Error Sistem", "Gagal memuat halaman Beranda. Cek log error.");
            }
            
        } else {
            Notifikasi.tampilkan("ERROR", "Gagal Masuk", "Username atau Password salah! Coba cek lagi ya.");
        }
    }
    
   
    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) LOGIN.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}