package ui;

import services.*;
import models.*;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame(UserService userService,
                      ProductService productService,
                      OrderService orderService) {

        setTitle("Shopping Mall - Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginBtn = new JButton("Sign in");
        JButton signupBtn = new JButton("Sign up");

        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(signupBtn);
        panel.add(loginBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword());

            User user = userService.login(u, p);

            if (user != null) {
                dispose();

                if (user instanceof Admin) {
                    new AdminFrame((Admin) user, productService, orderService, userService);
                } else if (user instanceof Customer) {
                    new ShopFrame(productService, orderService, user);
                }

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        signupBtn.addActionListener(e -> {
            new RegisterFrame(userService);
        });

        setVisible(true);
    }
}
