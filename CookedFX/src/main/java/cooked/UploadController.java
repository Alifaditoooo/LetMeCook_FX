package cooked;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    @FXML private Label lblNamaFile;
    @FXML private ImageView previewGambar;

    private File fileGambarDipilih;
    private LayananResep layanan = new LayananResep();

    @FXML
    private void pilihGambar() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Gambar Resep");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Buka dialog pilih file
        File file = fc.showOpenDialog(inputJudul.getScene().getWindow());
        
        if (file != null) {
            this.fileGambarDipilih = file;
            lblNamaFile.setText(file.getName());
            
            // Tampilkan preview
            Image image = new Image(file.toURI().toString());
            previewGambar.setImage(image);
        }
    }

    @FXML
    private void submitResep() {
        String judul = inputJudul.getText();
        String deskripsi = inputDeskripsi.getText();
        String bahanRaw = inputBahan.getText();
        String langkahRaw = inputLangkah.getText();
        
        if (judul.isEmpty() || deskripsi.isEmpty()) {
            showAlert("Error", "Judul dan Deskripsi wajib diisi!");
            return;
        }
        
        // Ubah text area menjadi List (dipisah baris baru)
        List<String> listBahan = Arrays.asList(bahanRaw.split("\\n"));
        List<String> listLangkah = Arrays.asList(langkahRaw.split("\\n"));
        
        // PANGGIL LAYANAN UNTUK UPLOAD & SIMPAN
        boolean sukses = layanan.buatResep(judul, deskripsi, listBahan, listLangkah, fileGambarDipilih);
        
        if (sukses) {
            showAlert("Sukses", "Resep berhasil di-upload!");
            tutupWindow();
        } else {
            showAlert("Gagal", "Terjadi kesalahan saat menyimpan resep.");
        }
    }

    @FXML
    private void batalUpload() {
        tutupWindow();
    }

    private void tutupWindow() {
        Stage stage = (Stage) inputJudul.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}