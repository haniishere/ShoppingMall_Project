package ui;

import models.*;
import services.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerPanel {
    private Scanner scanner;
    private Customer customer;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;
    private List<CartItem> shoppingCart;
    
    public CustomerPanel(Customer customer, ProductService productService,
                        OrderService orderService, UserService userService) {
        this.scanner = new Scanner(System.in);
        this.customer = customer;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.shoppingCart = new ArrayList<>();
    }
    
    public void show() {
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    browseAllProducts();
                    break;
                case 2:
                    browseByCategory();
                    break;
                case 3:
                    searchProducts();
                    break;
                case 4:
                    viewProductDetails();
                    break;
                case 5:
                    addToCart();
                    break;
                case 6:
                    viewCart();
                    break;
                case 7:
                    checkout();
                    break;
                case 8:
                    viewOrderHistory();
                    break;
                case 9:
                    viewProfile();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      CUSTOMER PANEL - " + customer.getUsername());
        System.out.println("=".repeat(50));
        System.out.println("1. Browse All Products");
        System.out.println("2. Browse by Category");
        System.out.println("3. Search Products");
        System.out.println("4. View Product Details");
        System.out.println("5. Add to Cart");
        System.out.println("6. View Shopping Cart (" + shoppingCart.size() + " items)");
        System.out.println("7. Checkout");
        System.out.println("8. View Order History");
        System.out.println("9. View Profile");
        System.out.println("0. Logout");
        System.out.println("=".repeat(50));
    }
    
    private void browseAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");
        List<Product> products = productService.getInStockProducts();
        
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        displayProductList(products);
    }
    
    private void browseByCategory() {
        System.out.println("\n--- BROWSE BY CATEGORY ---");
        
        List<String> categories = productService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }
        
        System.out.println("\nAvailable Categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        
        int choice = getIntInput("\nSelect category (1-" + categories.size() + "): ");
        if (choice < 1 || choice > categories.size()) {
            System.out.println("Invalid choice!");
            return;
        }
        
        String selectedCategory = categories.get(choice - 1);
        List<Product> products = productService.getProductsByCategory(selectedCategory);
        
        System.out.println("\n--- Products in " + selectedCategory + " ---");
        displayProductList(products);
    }
    
    private void searchProducts() {
        System.out.println("\n--- SEARCH PRODUCTS ---");
        
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        List<Product> results = productService.searchProductsByName(searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("No products found matching '" + searchTerm + "'");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        displayProductList(results);
    }
    
    private void viewProductDetails() {
        System.out.println("\n--- PRODUCT DETAILS ---");
        
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Product ID: " + product.getProductId());
        System.out.println("Name: " + product.getName());
        System.out.println("Category: " + product.getCategory());
        System.out.println("Price: $" + String.format("%.2f", product.getPrice()));
        System.out.println("Stock: " + product.getQuantity() + " units");
        System.out.println("Description: " + product.getDescription());
        System.out.println("Status: " + (product.isInStock() ? "In Stock" : "Out of Stock"));
        System.out.println("=".repeat(50));
    }
    
    private void addToCart() {
        System.out.println("\n--- ADD TO CART ---");
        
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        if (!product.isInStock()) {
            System.out.println("Sorry, this product is out of stock!");
            return;
        }
        
        System.out.println("\nProduct: " + product.getName());
        System.out.println("Price: $" + String.format("%.2f", product.getPrice()));
        System.out.println("Available Stock: " + product.getQuantity());
        
        int quantity = getIntInput("Enter quantity: ");
        
        if (quantity <= 0) {
            System.out.println("Invalid quantity!");
            return;
        }
        
        if (quantity > product.getQuantity()) {
            System.out.println("Sorry, only " + product.getQuantity() + " units available!");
            return;
        }
        
        // Check if product already in cart
        CartItem existingItem = null;
        for (CartItem item : shoppingCart) {
            if (item.getProduct().getProductId().equals(productId)) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
            System.out.println("✓ Updated cart! Total quantity: " + existingItem.getQuantity());
        } else {
            CartItem newItem = new CartItem(product, quantity);
            shoppingCart.add(newItem);
            System.out.println("✓ Added to cart!");
        }
    }
    
    private void viewCart() {
        System.out.println("\n--- SHOPPING CART ---");
        
        if (shoppingCart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        
        System.out.println(String.format("%-10s %-25s %-10s %-8s %-12s",
                          "ID", "Product", "Price", "Qty", "Subtotal"));
        System.out.println("-".repeat(70));
        
        double total = 0.0;
        for (CartItem item : shoppingCart) {
            Product p = item.getProduct();
            System.out.println(String.format("%-10s %-25s $%-9.2f %-8d $%-11.2f",
                              p.getProductId(),
                              truncate(p.getName(), 25),
                              p.getPrice(),
                              item.getQuantity(),
                              item.getTotalPrice()));
            total += item.getTotalPrice();
        }
        
        System.out.println("-".repeat(70));
        System.out.println(String.format("%57s $%.2f", "TOTAL:", total));
        
        System.out.println("\nOptions:");
        System.out.println("1. Remove item");
        System.out.println("2. Update quantity");
        System.out.println("3. Clear cart");
        System.out.println("0. Back to menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                removeFromCart();
                break;
            case 2:
                updateCartQuantity();
                break;
            case 3:
                clearCart();
                break;
        }
    }
    
    private void removeFromCart() {
        System.out.print("Enter Product ID to remove: ");
        String productId = scanner.nextLine();
        
        CartItem toRemove = null;
        for (CartItem item : shoppingCart) {
            if (item.getProduct().getProductId().equals(productId)) {
                toRemove = item;
                break;
            }
        }
        
        if (toRemove != null) {
            shoppingCart.remove(toRemove);
            System.out.println("✓ Item removed from cart.");
        } else {
            System.out.println("Item not found in cart!");
        }
    }
    
    private void updateCartQuantity() {
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        
        CartItem item = null;
        for (CartItem cartItem : shoppingCart) {
            if (cartItem.getProduct().getProductId().equals(productId)) {
                item = cartItem;
                break;
            }
        }
        
        if (item == null) {
            System.out.println("Item not found in cart!");
            return;
        }
        
        System.out.println("Current quantity: " + item.getQuantity());
        int newQuantity = getIntInput("Enter new quantity: ");
        
        if (newQuantity <= 0) {
            shoppingCart.remove(item);
            System.out.println("✓ Item removed from cart.");
        } else if (newQuantity <= item.getProduct().getQuantity()) {
            item.setQuantity(newQuantity);
            System.out.println("✓ Quantity updated.");
        } else {
            System.out.println("Sorry, only " + item.getProduct().getQuantity() + " units available!");
        }
    }
    
    private void clearCart() {
        System.out.print("Are you sure you want to clear the cart? (yes/no): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            shoppingCart.clear();
            System.out.println("✓ Cart cleared.");
        }
    }
    
    private void checkout() {
        System.out.println("\n--- CHECKOUT ---");
        
        if (shoppingCart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }
        
        // Display cart summary
        double total = 0.0;
        System.out.println("\nOrder Summary:");
        System.out.println("-".repeat(50));
        for (CartItem item : shoppingCart) {
            System.out.println(item);
            total += item.getTotalPrice();
        }
        System.out.println("-".repeat(50));
        System.out.println("Total Amount: $" + String.format("%.2f", total));
        
        // Get shipping address
        System.out.print("\nShipping Address [" + customer.getAddress() + "]: ");
        String shippingAddress = scanner.nextLine();
        if (shippingAddress.isEmpty()) {
            shippingAddress = customer.getAddress();
        }
        
        System.out.print("\nConfirm order? (yes/no): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Order cancelled.");
            return;
        }
        
        // Place order
        String orderId = orderService.placeOrder(customer, new ArrayList<>(shoppingCart), shippingAddress);
        
        if (orderId != null) {
            System.out.println("\n✓ Order placed successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: $" + String.format("%.2f", total));
            System.out.println("\nYour order will be delivered to:");
            System.out.println(shippingAddress);
            
            // Update user
            userService.updateUser(customer);
            
            // Clear cart
            shoppingCart.clear();
        } else {
            System.out.println("✗ Failed to place order. Please try again.");
        }
    }
    
    private void viewOrderHistory() {
        System.out.println("\n--- ORDER HISTORY ---");
        
        List<Order> orders = orderService.getOrdersByCustomerId(customer.getUserId());
        
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }
        
        System.out.println(String.format("%-12s %-12s %-15s %-20s",
                          "Order ID", "Total", "Status", "Date"));
        System.out.println("-".repeat(65));
        
        for (Order order : orders) {
            System.out.println(String.format("%-12s $%-11.2f %-15s %-20s",
                              order.getOrderId(),
                              order.getTotalAmount(),
                              order.getStatus(),
                              order.getOrderDate().toString().substring(0, 19)));
        }
        
        System.out.print("\nView order details? Enter Order ID (or press Enter to skip): ");
        String orderId = scanner.nextLine();
        
        if (!orderId.isEmpty()) {
            viewOrderDetails(orderId);
        }
    }
    
    private void viewOrderDetails(String orderId) {
        Order order = orderService.getOrderById(orderId);
        
        if (order == null || !order.getCustomerId().equals(customer.getUserId())) {
            System.out.println("Order not found!");
            return;
        }
        
        System.out.println("\n--- ORDER DETAILS ---");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Date: " + order.getOrderDate());
        System.out.println("Shipping Address: " + order.getShippingAddress());
        System.out.println("\nItems:");
        
        for (CartItem item : order.getItems()) {
            System.out.println("  • " + item);
        }
        
        System.out.println("\nTotal Amount: $" + String.format("%.2f", order.getTotalAmount()));
    }
    
    private void viewProfile() {
        System.out.println("\n--- MY PROFILE ---");
        System.out.println("User ID: " + customer.getUserId());
        System.out.println("Username: " + customer.getUsername());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhoneNumber());
        System.out.println("Address: " + customer.getAddress());
        System.out.println("Total Orders: " + customer.getOrderHistory().size());
    }
    
    // Helper methods
    private void displayProductList(List<Product> products) {
        System.out.println(String.format("%-10s %-30s %-15s %-10s %-8s",
                          "ID", "Name", "Category", "Price", "Stock"));
        System.out.println("-".repeat(80));
        
        for (Product product : products) {
            System.out.println(String.format("%-10s %-30s %-15s $%-9.2f %-8d",
                              product.getProductId(),
                              truncate(product.getName(), 30),
                              truncate(product.getCategory(), 15),
                              product.getPrice(),
                              product.getQuantity()));
        }
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
