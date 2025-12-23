package cooked;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    
    // Sesuaikan nama database dengan file SQL kamu: db_cooked
    private static final String URL = "jdbc:mysql://localhost:3306/db_cooked";
    private static final String USER = "root"; 
    private static final String PASS = "";     

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
        }
        return conn;
    }
}