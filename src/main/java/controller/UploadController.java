package controller;

import cooked.LayananResep;
import cooked.Notifikasi; 
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadController {

    // --- VARIABEL FXML (Sesuai dengan file upload.fxml Anda) ---
    @FXML private TextField txtJudul;       
    @FXML private TextArea txtDeskripsi;    
    @FXML private TextArea txtBahan;        
    @FXML private TextArea txtLangkah;      
    @FXML private ImageView imgPreview;     
    
    @FXML private Label lblKategoriTerpilih; 

    @FXML private Button btnKatRingan;      
    @FXML private Button btnKatBerat;       
    @FXML private Button btnKatMinuman;     
    
    @FXML private Button btnPilihFoto;
    @FXML private Button btnPost;

    // --- VARIABEL DATA ---
    private LayananResep layanan = new LayananResep();
    private File fileGambarTerpilih = null;
    private String kategoriDipilih = null; 

    @FXML
    public void initialize() {
        resetButtonStyles();
    }

    // --- VISUAL TOMBOL ---
    private void resetButtonStyles() {
        String styleMati = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ddd; -fx-cursor: hand;";
        if(btnKatRingan != null) btnKatRingan.setStyle(styleMati);
        if(btnKatBerat != null) btnKatBerat.setStyle(styleMati);
        if(btnKatMinuman != null) btnKatMinuman.setStyle(styleMati);
    }
    
    private void setTombolAktif(Button btn) {
        if(btn != null) {
            btn.setStyle("-fx-background-color: #FF451F; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #FF451F; -fx-cursor: hand;");
        }
    }
    
    private void updateLabelKategori() {
        if (lblKategoriTerpilih != null) {
            lblKategoriTerpilih.setText("Kategori: " + kategoriDipilih);
            lblKategoriTerpilih.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-style: italic;");
        }
    }

    // --- AKSI KATEGORI ---
    @FXML
    private void pilihKategoriRingan(ActionEvent event) {
        kategoriDipilih = "Makanan Ringan";
        resetButtonStyles();
        setTombolAktif(btnKatRingan);
        updateLabelKategori();
    }

    @FXML
    private void pilihKategoriBerat(ActionEvent event) {
        kategoriDipilih = "Makanan Berat";
        resetButtonStyles();
        setTombolAktif(btnKatBerat);
        updateLabelKategori();
    }

    @FXML
    private void pilihKategoriMinuman(ActionEvent event) {
        kategoriDipilih = "Minuman";
        resetButtonStyles();
        setTombolAktif(btnKatMinuman);
        updateLabelKategori();
    }

    // --- PILIH FOTO ---
    @FXML
    private void handlePilihFoto(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Gambar Resep");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Gambar", "*.jpg", "*.png", "*.jpeg"));
        
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            File file = fc.showOpenDialog(stage);
            
            if (file != null) {
                this.fileGambarTerpilih = file;
                if(imgPreview != null) {
                    imgPreview.setImage(new Image(file.toURI().toString()));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- POSTING RESEP (FULL VALIDASI) ---
    @FXML
    private void handlePost(ActionEvent event) {
        // 1. AMBIL DATA
        String judul = (txtJudul != null) ? txtJudul.getText() : "";
        String deskripsi = (txtDeskripsi != null) ? txtDeskripsi.getText() : "";
        String bahan = (txtBahan != null) ? txtBahan.getText() : "";
        String langkah = (txtLangkah != null) ? txtLangkah.getText() : "";

        // 2. SIAPKAN PENAMPUNG ERROR
        StringBuilder pesanError = new StringBuilder();

        // 3. CEK SEMUA KOLOM (6 item)
        if (judul.trim().isEmpty()) {
            pesanError.append("- Judul resep wajib diisi.\n");
        }
        
        if (deskripsi.trim().isEmpty()) {
            pesanError.append("- Deskripsi singkat belum diisi.\n");
        }
        
        if (kategoriDipilih == null) {
            pesanError.append("- Kategori belum dipilih.\n");
        }

        if (fileGambarTerpilih == null) {
            pesanError.append("- Foto masakan belum diupload.\n");
        }
        
        if (bahan.trim().isEmpty()) {
            pesanError.append("- Bahan-bahan tidak boleh kosong.\n");
        }
        
        if (langkah.trim().isEmpty()) {
            pesanError.append("- Langkah pembuatan tidak boleh kosong.\n");
        }

        // 4. JIKA ADA ERROR, TAMPILKAN SEMUANYA
        if (pesanError.length() > 0) {
            Notifikasi.tampilkan("ERROR", "Data Belum Lengkap", 
                "Mohon lengkapi data berikut:\n" + pesanError.toString());
            return; // STOP DI SINI
        }

        // --- JIKA LOLOS, LANJUT UPLOAD ---
        String namaFileGambar = null;
        try {
            File folder = new File("C:\\CookedUploads");
            if (!folder.exists()) folder.mkdirs();

            String ext = fileGambarTerpilih.getName().substring(fileGambarTerpilih.getName().lastIndexOf("."));
            namaFileGambar = "resep_" + System.currentTimeMillis() + ext;
            
            File dest = new File(folder, namaFileGambar);
            Files.copy(fileGambarTerpilih.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            e.printStackTrace();
            Notifikasi.tampilkan("ERROR", "Gagal Upload Gambar", "Terjadi kesalahan sistem saat menyimpan gambar.");
            return;
        }

        // SIMPAN KE DATABASE
        Resep resepBaru = new Resep();
        resepBaru.setJudul(judul);
        resepBaru.setDeskripsi(deskripsi);
        resepBaru.setBahan(bahan);
        resepBaru.setLangkah(langkah);
        resepBaru.setKategori(kategoriDipilih);
        resepBaru.setGambarFilename(namaFileGambar);
        
        layanan.tambahResepBaru(resepBaru);

        Notifikasi.tampilkan("SUCCESS", "Berhasil!", "Resep kamu berhasil diposting.");
        handleBack(null);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooked/beranda.fxml"));
            Parent root = loader.load();
            
            Stage stage = null;
            if (event != null && event.getSource() instanceof javafx.scene.Node) {
                 stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            } else if (txtJudul != null) {
                 stage = (Stage) txtJudul.getScene().getWindow();
            }
            
            if (stage != null) stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}