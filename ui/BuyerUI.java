package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.*;
import services.*;

public class BuyerUI extends JFrame {
    private ShoppingService shoppingService;
    private JTable productsTable;
    private JTable cartTable;
    private JLabel balanceLabel;
    private JLabel totalLabel;
    
    public BuyerUI(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
        initializeUI();
        loadProducts();
        loadCart();
        updateBalance();
    }
    
    private void initializeUI() {
        setTitle("Shopping Mall - Buyer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        // Top panel with balance and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            shoppingService.logout();
            dispose();
            new LoginUI();
        });
        topPanel.add(balanceLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        String[] productColumns = {"ID", "Name", "Description", "Category", "Price", "Stock"};
        DefaultTableModel productsModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };  
        productsTable = new JTable(productsModel);
        JScrollPane productsScrollPane = new JScrollPane(productsTable);
        productsPanel.add(productsScrollPane, BorderLayout.CENTER);

        //Add to cart panel
        JPanel addCartPanel = new JPanel(new FlowLayout());
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = (int) productsModel.getValueAt(selectedRow, 0);
                int quantity = (int) quantitySpinner.getValue();
                shoppingService.addToCart(productId, quantity);
                loadCart();
                loadProducts();
                JOptionPane.showMessageDialog(this, "Added to cart!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first");
            }
        });
        addCartPanel.add(new JLabel("Quantity:"));
        addCartPanel.add(quantitySpinner);
        addCartPanel.add(addToCartButton);
        productsPanel.add(addCartPanel, BorderLayout.SOUTH);
        
        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        String[] cartColumns = {"Product", "Price", "Quantity", "Total"};
        DefaultTableModel cartModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        JPanel cartButtonsPanel = new JPanel(new FlowLayout());
        JButton removeButton = new JButton("Remove Selected");
        JButton checkoutButton = new JButton("Checkout");
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                String productName = (String) cartModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Remove " + productName + " from cart?", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Find product ID
                    for (CartItem item : shoppingService.getCartItems()) {
                        if (item.getProduct().getName().equals(productName)) {
                            shoppingService.removeFromCart(item.getProduct().getId());
                            loadCart();
                            loadProducts();
                            break;
                        }
                    }
                }
            }
        });
        checkoutButton.addActionListener(e -> checkout());
        cartButtonsPanel.add(removeButton);
        cartButtonsPanel.add(checkoutButton);
        cartButtonsPanel.add(totalLabel);
        cartPanel.add(cartButtonsPanel, BorderLayout.SOUTH);
        

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, productsPanel, cartPanel);
        splitPane.setResizeWeight(0.6);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
    
    private void loadProducts() {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0);
        for (Product product : shoppingService.getAllProducts()) {
            if (product.getStockQuantity() > 0) {
                model.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getCategory().name(),
                    String.format("$%.2f", product.getPrice()),
                    product.getStockQuantity()
                });
            }
        }
    }
    
    private void loadCart() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0);
        double total = 0;
        for (CartItem item : shoppingService.getCartItems()) {
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            model.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("$%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("$%.2f", itemTotal)
            });
        }
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }
    
    private void updateBalance() {
        if (shoppingService.getCurrentUser() != null) {
            balanceLabel.setText("Balance: $" + 
                String.format("%.2f", shoppingService.getCurrentUser().getBalance()));
        }
    }
    
    private void checkout() {
        if (shoppingService.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }
        double total = shoppingService.getCartTotal();
        double balance = shoppingService.getCurrentUser().getBalance();
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Total: $%.2f\nYour balance: $%.2f\n\nConfirm checkout?", 
                total, balance),
            "Checkout Confirmation",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = shoppingService.checkout();
            if (order != null) {
                JOptionPane.showMessageDialog(this,
                    "Order confirmed!\nOrder ID: " + order.getOrderId() + 
                    "\nTotal: $" + String.format("%.2f", total));
                loadCart();
                loadProducts();
                updateBalance();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Checkout failed! Insufficient balance.");
            }
        }
    }
}