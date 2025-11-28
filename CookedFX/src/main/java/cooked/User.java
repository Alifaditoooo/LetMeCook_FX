package cooked;

public class User {
    private int userId; 
    private String username;
    private String password; 

    
    public User(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }
    
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    
    public int getId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}