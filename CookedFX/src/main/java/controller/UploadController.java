package controller;

import cooked.LayananResep;
import cooked.Resep;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadController {

    @FXML private ImageView imgPreview;
    @FXML private TextField txtJudul;
    @FXML private TextArea txtDeskripsi;
    @FXML private TextArea txtBahan;
    @FXML private TextArea txtLangkah;
    @FXML private Label lblKategoriTerpilih;
    
    @FXML private Button btnKatRingan;
    @FXML private Button btnKatBerat;
    @FXML private Button btnKatMinuman;
    @FXML private Button btnPost;

    private File fileGambarTerpilih;
    private String kategori = "";
    private LayananResep layanan = new LayananResep();
    
    private static final String FOLDER_GAMBAR = "C:\\CookedUploads";

    @FXML
    private void handlePilihFoto(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Foto Masakan");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Gambar", "*.jpg", "*.png", "*.jpeg"));
        
        File file = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if (file != null) {
            this.fileGambarTerpilih = file;
            imgPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    // --- PILIHAN KATEGORI ---
    @FXML private void pilihKategoriRingan() { setKategori("Makanan Ringan", btnKatRingan); }
    @FXML private void pilihKategoriBerat() { setKategori("Makanan Berat", btnKatBerat); }
    @FXML private void pilihKategoriMinuman() { setKategori("Minuman", btnKatMinuman); }

    private void setKategori(String kat, Button btnAktif) {
        this.kategori = kat;
        lblKategoriTerpilih.setText("Kategori: " + kat);
        
        resetButtonStyle(btnKatRingan);
        resetButtonStyle(btnKatBerat);
        resetButtonStyle(btnKatMinuman);
        
        btnAktif.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-border-color: #FF451F; -fx-cursor: hand;");
    }
    
    private void resetButtonStyle(Button btn) {
        btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ddd; -fx-cursor: hand;");
    }

    @FXML
    private void handlePost(ActionEvent event) {
        if (txtJudul.getText().isEmpty() || kategori.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setContentText("Judul dan Kategori wajib diisi!");
            alert.show();
            return;
        }

        String namaFileGambar = "";
        
        if (fileGambarTerpilih != null) {
            try {
                File folder = new File(FOLDER_GAMBAR);
                if (!folder.exists()) folder.mkdirs();
                
                namaFileGambar = "resep_" + System.currentTimeMillis() + ".jpg";
                File tujuan = new File(folder, namaFileGambar);
                
                Files.copy(fileGambarTerpilih.toPath(), tujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal upload gambar: " + e.getMessage());
                alert.show();
                return;
            }
        }

        Resep resepBaru = new Resep();
        resepBaru.setJudul(txtJudul.getText());
        resepBaru.setDeskripsi(txtDeskripsi.getText());
        resepBaru.setBahan(txtBahan.getText());
        resepBaru.setLangkah(txtLangkah.getText());
        resepBaru.setKategori(kategori);
        resepBaru.setGambarFilename(namaFileGambar);

        layanan.tambahResepBaru(resepBaru);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Sukses");
        info.setContentText("Resep berhasil diposting!");
        info.showAndWait();

        handleBack(null);
    }

    // --- KEMBALI KE BERANDA (CARA MANUAL / BIASA) ---
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load FXML Beranda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            
            // Ambil Stage dari elemen UI yang ada (misal txtJudul)
            Stage stage = (Stage) txtJudul.getScene().getWindow();
            
            // Ganti Scene
            stage.setScene(new Scene(root));
            
            // Pastikan tetap full screen
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) { 
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal kembali ke Beranda: " + e.getMessage());
            alert.show();
        }
    }
}