package cooked;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField insertUSER;

    @FXML
    private PasswordField insertPW;

    @FXML
    private Button LOGIN;
    
    @FXML
    private Button REGISTER;

    
    private LayananResep layanan = new LayananResep();

   
    @FXML
    private void handleLoginAction() {
        String username = insertUSER.getText();
        String password = insertPW.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanPesan("Error", "Username dan Password tidak boleh kosong!");
            return;
        }

        
        boolean berhasil = layanan.login(username, password);

        if (berhasil) {
            tampilkanPesan("Sukses", "Login Berhasil! Selamat datang " + username);
            pindahKeBeranda();
        } else {
            tampilkanPesan("Gagal", "Username atau Password salah, atau akun belum terdaftar.");
        }
    }

    
    @FXML
    private void handleRegisterAction() {
        String username = insertUSER.getText();
        String password = insertPW.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanPesan("Error", "Isi username dan password untuk mendaftar!");
            return;
        }

        
        boolean berhasil = layanan.register(username, password);

        if (berhasil) {
            tampilkanPesan("Sukses", "Akun berhasil dibuat! Silakan klik LOG IN untuk masuk.");
        } else {
            tampilkanPesan("Gagal", "Username mungkin sudah dipakai. Coba gunakan username lain.");
        }
    }

   
    private void pindahKeBeranda() {
        try {
      
            FXMLLoader loader = new FXMLLoader(getClass().getResource("beranda.fxml"));
            Parent root = loader.load();
            
            // Mendapatkan jendela (Stage) saat ini dari tombol login
            Stage stage = (Stage) LOGIN.getScene().getWindow();
            
            // Mengganti tampilan dengan Beranda
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            tampilkanPesan("Error Sistem", "Gagal memuat halaman Beranda: " + e.getMessage());
        }
    }

    
    private void tampilkanPesan(String judul, String isi) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(isi);
        alert.showAndWait();
    }
}