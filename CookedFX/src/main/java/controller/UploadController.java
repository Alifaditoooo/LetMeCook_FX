package controller;

import cooked.LayananResep;
import cooked.Resep;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadController {

    @FXML private TextField inputJudul;
    @FXML private TextArea inputDeskripsi;
    @FXML private TextArea inputBahan;
    @FXML private TextArea inputLangkah;
    @FXML private ImageView previewGambar;
    @FXML private Button btnPilihGambar;
    @FXML private Button btnUpload;
    // btnBatal tidak wajib ada fx:id nya di java kalau cuma dipakai di onAction, tapi boleh tetap ada.

    private File fileGambarTerpilih;
    private LayananResep layanan = new LayananResep();

    @FXML
    private void handlePilihGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Masakan");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(btnPilihGambar.getScene().getWindow());
        if (file != null) {
            fileGambarTerpilih = file;
            previewGambar.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleUpload() {
        // 1. Validasi Input
        if (inputJudul.getText().isEmpty() || inputDeskripsi.getText().isEmpty() || 
            inputBahan.getText().isEmpty() || inputLangkah.getText().isEmpty()) {
            
            tampilkanAlert("Error", "Mohon isi semua kolom (Judul, Deskripsi, Bahan, Langkah)!");
            return;
        }

        // 2. Buat Objek Resep Baru
        Resep resepBaru = new Resep();
        resepBaru.setJudul(inputJudul.getText());
        resepBaru.setDeskripsi(inputDeskripsi.getText());
        resepBaru.setPenulisUsername("Chef Kamu"); 
        resepBaru.setJumlahLike(0);
        
        // Simpan text dari inputan Chef
        resepBaru.setBahan(inputBahan.getText());
        resepBaru.setLangkah(inputLangkah.getText());

        // 3. Proses Simpan Gambar
        if (fileGambarTerpilih != null) {
            try {
                File folderUpload = new File("C:\\CookedUploads");
                if (!folderUpload.exists()) folderUpload.mkdir();

                String namaFileBaru = System.currentTimeMillis() + "_" + fileGambarTerpilih.getName();
                File tujuan = new File(folderUpload, namaFileBaru);

                Files.copy(fileGambarTerpilih.toPath(), tujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
                resepBaru.setGambarFilename(namaFileBaru);

            } catch (IOException e) {
                tampilkanAlert("Gagal", "Gagal mengupload gambar: " + e.getMessage());
                return;
            }
        }

        // 4. Simpan ke Database Sementara
        layanan.tambahResepBaru(resepBaru);

        tampilkanAlert("Sukses", "Resep berhasil diposting!");
        kembaliKeBeranda(); // Pindah halaman otomatis
    }

    @FXML
    private void handleBatal() {
        kembaliKeBeranda();
    }

    private void kembaliKeBeranda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            
         
            Stage stage = (Stage) inputJudul.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}