package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.CartItem;
import models.Customer;
import models.Product;
import models.User;
import services.*;

public class ShopFrame extends JFrame {

    private JLabel balanceLabel;
    private List<CartItem> cartItems = new ArrayList<>();

    private JPanel productsPanel;
    private ProductService productService;
    private OrderService orderService;
    private Customer customer;

    public ShopFrame(ProductService productService,
                     OrderService orderService,
                     User user) {

        if (!(user instanceof Customer)) {
            JOptionPane.showMessageDialog(this, "Only customers can shop!");
            return;
        }

        this.productService = productService;
        this.orderService = orderService;
        this.customer = (Customer) user;

        setTitle("Shopping Mall");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

         // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());

        balanceLabel = new JLabel("Balance: $" + customer.getBalance());
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(balanceLabel, BorderLayout.WEST);

        JButton cartBtn = new JButton("View Cart");
        topPanel.add(cartBtn, BorderLayout.CENTER);

        JButton checkoutBtn = new JButton("Checkout");
        topPanel.add(checkoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

         //  SEARCH + SORT
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        JComboBox<String> sortBox = new JComboBox<>(
                new String[]{"Sort by Name ↑", "Sort by Name ↓", "Sort by Price ↑", "Sort by Price ↓"}
        );

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchBtn);
        controlPanel.add(sortBox);

        add(controlPanel, BorderLayout.SOUTH);

         // PRODUCTS PANEL
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));

        refreshProducts(productService.getAllProducts());

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        add(scrollPane, BorderLayout.CENTER);

         // EVENTS
         searchBtn.addActionListener(e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) {
                refreshProducts(productService.getAllProducts());
            } else {
                refreshProducts(productService.searchProductsByName(term));
            }
        });
        
        cartBtn.addActionListener(e ->
            new CartDialog(this, cartItems)
        );



        sortBox.addActionListener(e -> {
            int index = sortBox.getSelectedIndex();
            List<Product> sorted;

            switch (index) {
                case 0:
                    sorted = productService.sortByName(true);
                    break;
                case 1:
                    sorted = productService.sortByName(false);
                    break;
                case 2:
                    sorted = productService.sortByPrice(true);
                    break;
                case 3:
                    sorted = productService.sortByPrice(false);
                    break;
                default:
                    sorted = productService.getAllProducts();
                    break;
            }

            refreshProducts(sorted);
        });

        checkoutBtn.addActionListener(e -> checkout());

        setVisible(true);
    }

     // HELPER METHODS

    private void refreshProducts(List<Product> products) {
        productsPanel.removeAll();

        for (Product p : products) {

            JPanel card = new JPanel(new GridLayout(2, 4));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            card.add(new JLabel("Title: " + p.getName()));
            card.add(new JLabel("Price: $" + p.getPrice()));
            card.add(new JLabel("Stock: " + p.getQuantity()));

            JTextField qtyField = new JTextField("1");
            card.add(qtyField);

            JButton addBtn = new JButton("Add to cart");
            card.add(addBtn);

            addBtn.addActionListener(e -> {
                try {
                    int qty = Integer.parseInt(qtyField.getText());

                    if (qty <= 0 || qty > p.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Invalid quantity");
                        return;
                    }

                    cartItems.add(new CartItem(p, qty));
                    JOptionPane.showMessageDialog(this, "Added to cart");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Enter a valid number");
                }
            });

            productsPanel.add(card);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        String address = JOptionPane.showInputDialog(this, "Shipping address:");
        if (address == null || address.isBlank()) return;

        String orderId = orderService.placeOrder(customer, cartItems, address);

        if (orderId == null) {
            JOptionPane.showMessageDialog(this, "Insufficient balance or stock!");
        } else {
            JOptionPane.showMessageDialog(this, "Order placed: " + orderId);
            cartItems.clear();
            balanceLabel.setText("Balance: $" + customer.getBalance());
        }
    }
}
