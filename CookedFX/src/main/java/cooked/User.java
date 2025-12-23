package cooked;

public class User {
    private int id; // Menyimpan user_id dari database
    private String username;
    private String password;
    
    // Constructor Kosong
    public User() {}

    // Constructor Lengkap
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Constructor Lama (untuk register tanpa ID)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}