package ui;

import services.*;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private ShoppingService shoppingService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public LoginUI() {
        this.shoppingService = new ShoppingService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Welcome to the shopping mall!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,300);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JLabel titleLabel = new JLabel("Shopping Mall Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial",Font.BOLD,20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(3,2,10,10));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        formPanel.add(loginButton);
        formPanel.add(registerButton);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegistrationDialog());
        passwordField.addActionListener(e -> login());
        add(mainPanel);
        setVisible(true);
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        if(shoppingService.login(username, password)){
            dispose();
            if(shoppingService.isBuyer()){
                new BuyerUI(shoppingService);
            }
            else if(shoppingService.isSeller()){
                new SellerUI(shoppingService);
            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }
    
    private void showRegistrationDialog(){
        JDialog registerDialog = new JDialog(this,"Register",true);
        registerDialog.setSize(300,250);
        registerDialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridLayout(5,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JTextField regUsernameField = new JTextField();
        JPasswordField regPasswordField = new JPasswordField();
        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"BUYER", "SELLER"});
        panel.add(new JLabel("Username:"));
        panel.add(regUsernameField);
        panel.add(new JLabel("Password:"));
        panel.add(regPasswordField);
        panel.add(new JLabel("User Type:"));
        panel.add(userTypeCombo);
        JButton cancelButton = new JButton("Cancel");
        JButton registerButton = new JButton("Register");
        panel.add(cancelButton);
        panel.add(registerButton);
        cancelButton.addActionListener(e -> registerDialog.dispose());
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String type = (String) userTypeCombo.getSelectedItem();
            if(username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(registerDialog,"Please fill all fields");
                return;
            }
            boolean success = shoppingService.registerUser(
                username, 
                password, 
                models.User.UserType.valueOf(type)
            );
            if(success){
                JOptionPane.showMessageDialog(registerDialog, "Registration successful!");
                registerDialog.dispose();
            }
            else{
                JOptionPane.showMessageDialog(registerDialog, "Username already exists!");
            }
        });
        registerDialog.add(panel);
        registerDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}