package models;

public class Admin extends User {
    private static final long serialVersionUID = 1L;
    
    private String role;
    
    // Constructors
    public Admin() {
        super();
        this.role = "Administrator";
    }
    
    public Admin(String userId, String username, String password, String email, String phoneNumber) {
        super(userId, username, password, email, phoneNumber);
        this.role = "Administrator";
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String getUserType() {
        return "ADMIN";
    }
    
    @Override
    public String toString() {
        return super.toString() + " | Role: " + role;
    }
    
    // Convert format
    public String toCSV() {
        return String.format("ADMIN,%s,%s,%s,%s,%s,%s",
                userId, username, password, email, phoneNumber, role);
    }
    
    // Create Admin 
    public static Admin fromCSV(String csv) {
        String[] parts = csv.split(",", 7);
        if (parts.length >= 6 && parts[0].equals("ADMIN")) {
            Admin admin = new Admin(
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                parts[5].trim()
            );
            if (parts.length == 7) {
                admin.setRole(parts[6].trim());
            }
            return admin;
        }
        return null;
    }
}
