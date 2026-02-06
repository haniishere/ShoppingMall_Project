package ui;

import models.Customer;
import services.UserService;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    public RegisterFrame(UserService userService) {

        setTitle("Sign Up");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        JButton registerBtn = new JButton("Register");

        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Password:"));
        add(passwordField);

        add(new JLabel("Email:"));
        add(emailField);

        add(new JLabel("Phone:"));
        add(phoneField);

        add(new JLabel());
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            String id = userService.generateNextCustomerId();

            Customer customer = new Customer(
                    id,
                    username,
                    password,
                    email,
                    phone,
                    ""
            );

            boolean success = userService.registerCustomer(customer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }
        });

        setVisible(true);
    }
}
