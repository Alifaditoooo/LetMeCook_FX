package cooked;

public class User {
    private int id;
    private String username;
    private String password;
    private String fotoProfil; 

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
    
    
    public User(int id, String username, String password, String fotoProfil) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fotoProfil = fotoProfil;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    
    public String getFotoProfil() { return fotoProfil; }
    public void setFotoProfil(String fotoProfil) { this.fotoProfil = fotoProfil; }
}