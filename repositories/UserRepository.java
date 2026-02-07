package repositories;

import models.User;
import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String CSV_FILE = "users.csv";
    private static final String HEADER = "username,password,type,balance";
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length == 4) {
                    User user = new User(
                        values[0],
                        values[1],
                        User.UserType.valueOf(values[2]),
                        Double.parseDouble(values[3])
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.out.println("Users CSV file not found, creating new one");
            initializeCSV();
            return getAllUsers();
        }
        
        return users;
    }
    
    public User getUserByUsername(String username) {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
    
    public void addUser(User user) {
        List<User> users = getAllUsers();
        users.add(user);
        saveAllUsers(users);
    }
    
    public void updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                break;
            }
        }
        saveAllUsers(users);
    }
    
    private void saveAllUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println(HEADER);
            for (User user : users) {
                pw.println(String.format("%s,%s,%s,%.2f",
                    user.getUsername(),
                    user.getPassword(),
                    user.getType().name(),
                    user.getBalance()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println(HEADER);
            
            List<User> sampleUsers = Arrays.asList(
                new User("buyer", "123", User.UserType.BUYER, 1000.00),
                new User("seller", "123", User.UserType.SELLER, 0.00)
            );
            
            for (User user : sampleUsers) {
                pw.println(String.format("%s,%s,%s,%.2f",
                    user.getUsername(),
                    user.getPassword(),
                    user.getType().name(),
                    user.getBalance()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}