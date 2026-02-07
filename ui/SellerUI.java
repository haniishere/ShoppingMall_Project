package ui;

import models.*;
import services.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SellerUI extends JFrame {
    private ShoppingService shoppingService;
    private JTable productsTable;
    private DefaultTableModel productsModel;
    
    public SellerUI(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
        initializeUI();
        loadProducts();
    }
    
    private void initializeUI() {
        setTitle("Shopping Mall - Seller Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            shoppingService.logout();
            dispose();
            new LoginUI();
        });
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        String[] columns = {"ID", "Name", "Description", "Category", "Price", "Stock"};
        productsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(productsModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton refreshButton = new JButton("Refresh");
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> loadProducts());
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }
    
    private void loadProducts() {
        productsModel.setRowCount(0);
        for (Product product : shoppingService.getAllProducts()) {
            productsModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory().name(),
                String.format("$%.2f", product.getPrice()),
                product.getStockQuantity()
            });
        }
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField nameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(2, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<Product.Category> categoryCombo = new JComboBox<>();
        for (Product.Category category : Product.getAllCategories()) {
            categoryCombo.addItem(category);
        }
        JTextField priceField = new JTextField();
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockSpinner);
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        panel.add(cancelButton);
        panel.add(saveButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                Product.Category category = (Product.Category) categoryCombo.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = (int) stockSpinner.getValue();
                
                if (name.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }
                
                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be greater than 0");
                    return;
                }
                
                Product product = new Product(0, name, description, category, price, stock);
                shoppingService.addProduct(product);
                loadProducts();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price format");
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showEditProductDialog() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit");
            return;
        }
        int productId = (int) productsModel.getValueAt(selectedRow, 0);
        Product product = shoppingService.getAllProducts().stream()
            .filter(p -> p.getId() == productId)
            .findFirst()
            .orElse(null);
        if (product == null) return;
        JDialog dialog = new JDialog(this, "Edit Product", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField nameField = new JTextField(product.getName());
        JTextArea descriptionArea = new JTextArea(product.getDescription(), 2, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<Product.Category> categoryCombo = new JComboBox<>();
        for (Product.Category category : Product.getAllCategories()) {
            categoryCombo.addItem(category);
        }
        categoryCombo.setSelectedItem(product.getCategory());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(product.getStockQuantity(), 0, 1000, 1));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockSpinner);
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        panel.add(cancelButton);
        panel.add(saveButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                Product.Category category = (Product.Category) categoryCombo.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = (int) stockSpinner.getValue();
                if (name.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }
                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be greater than 0");
                    return;
                }
                product.setName(name);
                product.setDescription(description);
                product.setCategory(category);
                product.setPrice(price);
                product.setStockQuantity(stock);
                shoppingService.updateProduct(product);
                loadProducts();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price format");
            }
        });
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete");
            return;
        }
        int productId = (int) productsModel.getValueAt(selectedRow, 0);
        String productName = (String) productsModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + productName + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            shoppingService.deleteProduct(productId);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
        }
    }
}