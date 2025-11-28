/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cooked;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi{
    
    
    
    
    private static final String IP_SERVER = "localhost"; 
    private static final String NAMA_DATABASE = "db_cooked";
    
   
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    // --- ------------------------------ ---
    
    
    private static final String URL = "jdbc:mysql://" + IP_SERVER + ":3306/" + NAMA_DATABASE;
    private static Connection koneksi;

    
    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
              
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                
               
                koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi ke database " + NAMA_DATABASE + " berhasil!");
                
            } catch (SQLException e) {
                System.out.println("GAGAL KONEK KE DATABASE: " + e.getMessage());
                
            }
        }
        return koneksi;
    }
}
