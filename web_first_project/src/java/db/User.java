package db;

import java.io.Serializable;

public class User implements Serializable {
    public static enum Role {BUYER, SELLER};
    
    private int id;
    private String username;
    private Role role;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    
}
