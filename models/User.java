package models;

public class User {
    private String username;
    private String password;
    private UserType type;
    private double balance;
    
    public enum UserType {
        BUYER,
        SELLER
    }
    
    public User(String username, String password, UserType type, double balance) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.balance = balance;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserType getType() { return type; }
    public void setType(UserType type) { this.type = type; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public void deductBalance(double amount) {
        this.balance -= amount;
    }
    public void addBalance(double amount) {
        this.balance += amount;
    }
}