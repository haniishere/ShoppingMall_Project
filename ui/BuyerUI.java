package ui;

import models.*;
import services.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class BuyerUI extends JFrame {
    private ShoppingService shoppingService;
    private JTable productsTable;
    private JTable cartTable;
    private JLabel balanceLabel;
    private JLabel totalLabel;
    private DefaultTableModel productsModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private List<Product> allProducts;
    private JPanel productsPanel; // Add this as instance variable
    
    public BuyerUI(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
        this.allProducts = shoppingService.getAllProducts();
        initializeUI();
        loadProducts();
        loadCart();
        updateBalance();
    }
    
    private void initializeUI() {
        setTitle("Shopping Mall - Buyer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftTopPanel.add(balanceLabel);
        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            shoppingService.logout();
            dispose();
            new LoginUI();
        });
        rightTopPanel.add(logoutButton);
        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(rightTopPanel, BorderLayout.EAST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Products"));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.setToolTipText("Search products by name or description");
        JLabel categoryLabel = new JLabel("Category:");
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("All Categories");
        for (Product.Category category : Product.getAllCategories()) {
            categoryFilterCombo.addItem(category.name());
        }
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryFilterCombo);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        String[] productColumns = {"ID", "Name", "Description", "Category", "Price", "Stock"};
        productsModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(productsModel);
        tableSorter = new TableRowSorter<>(productsModel);
        productsTable.setRowSorter(tableSorter);
        JScrollPane productsScrollPane = new JScrollPane(productsTable);
        productsPanel.add(productsScrollPane, BorderLayout.CENTER);
        JPanel addCartPanel = new JPanel(new FlowLayout());
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = productsTable.convertRowIndexToModel(selectedRow);
                int productId = (int) productsModel.getValueAt(modelRow, 0);
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
        JButton clearCartButton = new JButton("Clear Cart");
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                String productName = (String) cartTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Remove " + productName + " from cart?", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
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
        clearCartButton.addActionListener(e -> {
            if (!shoppingService.getCartItems().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Clear all items from cart?",
                    "Clear Cart",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    shoppingService.clearCart();
                    loadCart();
                    JOptionPane.showMessageDialog(this, "Cart cleared!");
                }
            }
        });
        cartButtonsPanel.add(removeButton);
        cartButtonsPanel.add(clearCartButton);
        cartButtonsPanel.add(checkoutButton);
        cartButtonsPanel.add(Box.createHorizontalStrut(20));
        cartButtonsPanel.add(totalLabel);
        cartPanel.add(cartButtonsPanel, BorderLayout.SOUTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(productsPanel, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerPanel, cartPanel);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(450);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        setupSearchFunctionality(searchButton, clearButton);
        add(mainPanel);
        setVisible(true);
    }
    
    private void setupSearchFunctionality(JButton searchButton, JButton clearButton) {
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            loadProducts();
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        categoryFilterCombo.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) {
                filterByCategory();
            } else {
                performSearch();
            }
        });
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        RowFilter<DefaultTableModel, Integer> rowFilter = new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String name = entry.getStringValue(1).toLowerCase();
                String description = entry.getStringValue(2).toLowerCase();
                String category = entry.getStringValue(3);
                boolean matchesText = searchText.isEmpty() || 
                                     name.contains(searchText) || 
                                     description.contains(searchText);
                boolean matchesCategory = selectedCategory.equals("All Categories") || 
                                         category.equals(selectedCategory);
                return matchesText && matchesCategory;
            }
        };
        
        tableSorter.setRowFilter(rowFilter);
        int visibleRows = productsTable.getRowCount();
        int totalRows = productsModel.getRowCount();
        if (!searchText.isEmpty() || !selectedCategory.equals("All Categories")) {
            productsPanel.setBorder(BorderFactory.createTitledBorder(
                String.format("Available Products (Showing %d of %d)", visibleRows, totalRows)
            ));
        } else {
            productsPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        }
    }
    
    private void filterByCategory() {
        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        if (selectedCategory.equals("All Categories")) {
            tableSorter.setRowFilter(null);
            productsPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        } else {
            RowFilter<DefaultTableModel, Integer> categoryFilter = RowFilter.regexFilter("^" + selectedCategory + "$", 3);
            tableSorter.setRowFilter(categoryFilter);
            
            int visibleRows = productsTable.getRowCount();
            productsPanel.setBorder(BorderFactory.createTitledBorder(
                String.format("Available Products - %s (%d items)", selectedCategory, visibleRows)
            ));
        }
    }
    
    private void loadProducts() {
        productsModel.setRowCount(0);
        allProducts = shoppingService.getAllProducts(); // Refresh products list
        for (Product product : allProducts) {
            if (product.getStockQuantity() > 0) {
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
        searchField.setText("");
        categoryFilterCombo.setSelectedIndex(0);
        tableSorter.setRowFilter(null);
        productsPanel.setBorder(BorderFactory.createTitledBorder(
            String.format("Available Products (%d items)", productsModel.getRowCount())
        ));
    }

    private void loadCart() {
        DefaultTableModel cartModel = (DefaultTableModel) cartTable.getModel();
        cartModel.setRowCount(0);
        double total = 0;
        for (CartItem item : shoppingService.getCartItems()) {
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            cartModel.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("$%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("$%.2f", itemTotal)
            });
        }
        totalLabel.setText("Total: $" + String.format("%.2f", total));
        updateBalance();
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
        StringBuilder summary = new StringBuilder();
        summary.append("Cart Summary:\n");
        summary.append("----------------\n");
        for (CartItem item : shoppingService.getCartItems()) {
            summary.append(String.format("%s x%d: $%.2f\n", 
                item.getProduct().getName(),
                item.getQuantity(),
                item.getTotalPrice()));
        }
        summary.append("----------------\n");
        double total = shoppingService.getCartTotal();
        double balance = shoppingService.getCurrentUser().getBalance();
        summary.append(String.format("Total: $%.2f\n", total));
        summary.append(String.format("Your balance: $%.2f\n\n", balance));
        if (total > balance) {
            summary.append("Not enough balance!\n");
            summary.append(String.format("You need $%.2f more.", (total - balance)));
        } else {
            summary.append("Enough balance available.\n");
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            summary.toString(),
            "Checkout Confirmation",
            JOptionPane.YES_NO_OPTION,
            total > balance ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION && total <= balance) {
            Order order = shoppingService.checkout();
            if (order != null) {
                String receipt = String.format(
                    "Order Confirmed!\n\n" +
                    "Order ID: %d\n" +
                    "Date: %s\n" +
                    "Total: $%.2f\n" +
                    "Remaining Balance: $%.2f\n\n" +
                    "Thank you for your purchase!",
                    order.getOrderId(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderDate()),
                    total,
                    shoppingService.getCurrentUser().getBalance()
                );
                JOptionPane.showMessageDialog(this, receipt, "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
                loadCart();
                loadProducts();
                updateBalance();
            }
        }
    }
}