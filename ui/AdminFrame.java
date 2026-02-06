package ui;

import models.Admin;
import models.Product;
import services.ProductService;
import services.OrderService;
import services.UserService;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    private Admin admin;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;

    public AdminFrame(Admin admin,
                      ProductService productService,
                      OrderService orderService,
                      UserService userService) {

        this.admin = admin;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;

        setTitle("Admin Panel - " + admin.getUsername());
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fields
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField descriptionField = new JTextField();

        JButton addBtn = new JButton("Add Product");

        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("ID"));
        add(idField);

        add(new JLabel("Name"));
        add(nameField);

        add(new JLabel("Category"));
        add(categoryField);

        add(new JLabel("Price"));
        add(priceField);

        add(new JLabel("Quantity"));
        add(quantityField);

        add(new JLabel("Description"));
        add(descriptionField);

        add(new JLabel());
        add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                Product p = new Product(
                        idField.getText().trim(),
                        nameField.getText().trim(),
                        categoryField.getText().trim(),
                        Double.parseDouble(priceField.getText().trim()),
                        Integer.parseInt(quantityField.getText().trim()),
                        descriptionField.getText().trim()
                );

                productService.addProduct(p);
                JOptionPane.showMessageDialog(this, "Product added!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Price and Quantity must be numbers",
                        "Invalid input",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setVisible(true);
    }
}
