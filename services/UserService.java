package services;

import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.Customer;
import models.User;

public class UserService {
    
    private List<User> users;
    
    public UserService() {
        FileManager.initialize();
        this.users = new ArrayList<>();
        loadUsers();
        // Create default admin if no users exist
        if (users.isEmpty()) {
            createDefaultAdmin();
        }
    }
    
    // Create default admin account
    private void createDefaultAdmin() {
        Admin defaultAdmin = new Admin("A0001", "admin", "admin123",
                "admin@store.com", "0000000000");
        users.add(defaultAdmin);
        saveUsers();
        System.out.println("Default admin account created (username: admin, password: admin123)");
    }
    
    // Load users from file
    public void loadUsers() {
        users.clear();
        List<String> lines = FileManager.readLines(FileManager.getUsersFile());
        for (String line : lines) {
            if (line.startsWith("ADMIN")) {
                Admin admin = Admin.fromCSV(line);
                if (admin != null) {
                    users.add(admin);
                }
            } else if (line.startsWith("CUSTOMER")) {
                Customer customer = Customer.fromCSV(line);
                if (customer != null) {
                    users.add(customer);
                }
            }
        }
    }
    
    // Save all users to file
    public void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Admin) {
                lines.add(((Admin) user).toCSV());
            } else if (user instanceof Customer) {
                lines.add(((Customer) user).toCSV());
            }
        }
        FileManager.writeLines(FileManager.getUsersFile(), lines);
    }
    
    // Register a new customer
    public boolean registerCustomer(Customer customer) {
        if (getUserByUsername(customer.getUsername()) != null) {
            System.out.println("Username already exists!");
            return false;
        }
        users.add(customer);
        saveUsers();
        return true;
    }
    
    // Login 
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    // Get user by username
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    // Get user by ID
    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Customer) {
                customers.add((Customer) user);
            }
        }
        return customers;
    }
    
    // Get all admins
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Admin) {
                admins.add((Admin) user);
            }
        }
        return admins;
    }
    
    // Update user information
    public boolean updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                users.set(i, updatedUser);
                saveUsers();
                return true;
            }
        }
        return false;
    }
    
    // Change password
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            saveUsers();
            return true;
        }
        return false;
    }
    
    // Generate next customer ID
    public String generateNextCustomerId() {
        int maxId = 0;
        for (User user : users) {
            if (user instanceof Customer) {
                try {
                    String idNum = user.getUserId().replaceAll("[^0-9]", "");
                    int id = Integer.parseInt(idNum);
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return "C" + String.format("%04d", maxId + 1);
    }
}
