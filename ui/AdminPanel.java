package ui;

import models.*;
import services.*;
import java.util.List;
import java.util.Scanner;

public class AdminPanel {
    private Scanner scanner;
    private Admin admin;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;
    
    public AdminPanel(Admin admin, ProductService productService, 
                     OrderService orderService, UserService userService) {
        this.scanner = new Scanner(System.in);
        this.admin = admin;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }
    
    public void show() {
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    viewAllProducts();
                    break;
                case 2:
                    addProduct();
                    break;
                case 3:
                    editProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    viewInventoryStatus();
                    break;
                case 6:
                    viewAllOrders();
                    break;
                case 7:
                    updateOrderStatus();
                    break;
                case 8:
                    viewSalesReport();
                    break;
                case 9:
                    viewCustomers();
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
        System.out.println("         ADMIN PANEL - " + admin.getUsername());
        System.out.println("=".repeat(50));
        System.out.println("1. View All Products");
        System.out.println("2. Add New Product");
        System.out.println("3. Edit Product");
        System.out.println("4. Delete Product");
        System.out.println("5. View Inventory Status");
        System.out.println("6. View All Orders");
        System.out.println("7. Update Order Status");
        System.out.println("8. View Sales Report");
        System.out.println("9. View Customers");
        System.out.println("0. Logout");
        System.out.println("=".repeat(50));
    }
    
    private void viewAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");
        List<Product> products = productService.getAllProducts();
        
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        System.out.println(String.format("%-10s %-25s %-15s %-10s %-8s", 
                          "ID", "Name", "Category", "Price", "Stock"));
        System.out.println("-".repeat(75));
        
        for (Product product : products) {
            System.out.println(String.format("%-10s %-25s %-15s $%-9.2f %-8d",
                              product.getProductId(),
                              truncate(product.getName(), 25),
                              truncate(product.getCategory(), 15),
                              product.getPrice(),
                              product.getQuantity()));
        }
    }
    
    private void addProduct() {
        System.out.println("\n--- ADD NEW PRODUCT ---");
        
        String productId = productService.generateNextProductId();
        System.out.println("Product ID: " + productId);
        
        System.out.print("Product Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Category: ");
        String category = scanner.nextLine();
        
        double price = getDoubleInput("Price: $");
        int quantity = getIntInput("Initial Quantity: ");
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        Product product = new Product(productId, name, category, price, quantity, description);
        
        if (productService.addProduct(product)) {
            System.out.println("✓ Product added successfully!");
        } else {
            System.out.println("✗ Failed to add product.");
        }
    }
    
    private void editProduct() {
        System.out.println("\n--- EDIT PRODUCT ---");
        
        System.out.print("Enter Product ID to edit: ");
        String productId = scanner.nextLine();
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        System.out.println("\nCurrent Product Details:");
        System.out.println(product);
        System.out.println("\nEnter new values (press Enter to keep current value):");
        
        System.out.print("Name [" + product.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) product.setName(name);
        
        System.out.print("Category [" + product.getCategory() + "]: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) product.setCategory(category);
        
        System.out.print("Price [" + product.getPrice() + "]: $");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) {
            try {
                product.setPrice(Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price, keeping current value.");
            }
        }
        
        System.out.print("Quantity [" + product.getQuantity() + "]: ");
        String qtyStr = scanner.nextLine();
        if (!qtyStr.isEmpty()) {
            try {
                product.setQuantity(Integer.parseInt(qtyStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity, keeping current value.");
            }
        }
        
        System.out.print("Description [" + product.getDescription() + "]: ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) product.setDescription(description);
        
        if (productService.updateProduct(product)) {
            System.out.println("✓ Product updated successfully!");
        } else {
            System.out.println("✗ Failed to update product.");
        }
    }
    
    private void deleteProduct() {
        System.out.println("\n--- DELETE PRODUCT ---");
        
        System.out.print("Enter Product ID to delete: ");
        String productId = scanner.nextLine();
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        System.out.println("\nProduct to delete:");
        System.out.println(product);
        System.out.print("Are you sure? (yes/no): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            if (productService.deleteProduct(productId)) {
                System.out.println("✓ Product deleted successfully!");
            } else {
                System.out.println("✗ Failed to delete product.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void viewInventoryStatus() {
        System.out.println("\n--- INVENTORY STATUS ---");
        
        List<Product> lowStock = productService.getLowStockProducts();
        List<Product> outOfStock = productService.getOutOfStockProducts();
        
        System.out.println("\n⚠ Low Stock Products (Quantity < 10):");
        if (lowStock.isEmpty()) {
            System.out.println("None");
        } else {
            for (Product p : lowStock) {
                System.out.println("  • " + p.getName() + " - Quantity: " + p.getQuantity());
            }
        }
        
        System.out.println("\n✗ Out of Stock Products:");
        if (outOfStock.isEmpty()) {
            System.out.println("None");
        } else {
            for (Product p : outOfStock) {
                System.out.println("  • " + p.getName());
            }
        }
        
        System.out.println("\nTotal Products: " + productService.getAllProducts().size());
        System.out.println("In Stock: " + productService.getInStockProducts().size());
    }
    
    private void viewAllOrders() {
        System.out.println("\n--- ALL ORDERS ---");
        List<Order> orders = orderService.getAllOrders();
        
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        System.out.println(String.format("%-12s %-12s %-12s %-15s %-20s",
                          "Order ID", "Customer", "Total", "Status", "Date"));
        System.out.println("-".repeat(75));
        
        for (Order order : orders) {
            System.out.println(String.format("%-12s %-12s $%-11.2f %-15s %-20s",
                              order.getOrderId(),
                              order.getCustomerId(),
                              order.getTotalAmount(),
                              order.getStatus(),
                              order.getOrderDate().toString().substring(0, 19)));
        }
    }
    
    private void updateOrderStatus() {
        System.out.println("\n--- UPDATE ORDER STATUS ---");
        
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        
        System.out.println("\nCurrent Order:");
        System.out.println(order);
        System.out.println("\nAvailable statuses:");
        System.out.println("1. PENDING");
        System.out.println("2. PROCESSING");
        System.out.println("3. SHIPPED");
        System.out.println("4. DELIVERED");
        System.out.println("5. CANCELLED");
        
        int choice = getIntInput("Select new status (1-5): ");
        String newStatus = "";
        
        switch (choice) {
            case 1: newStatus = "PENDING"; break;
            case 2: newStatus = "PROCESSING"; break;
            case 3: newStatus = "SHIPPED"; break;
            case 4: newStatus = "DELIVERED"; break;
            case 5: newStatus = "CANCELLED"; break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        
        if (orderService.updateOrderStatus(orderId, newStatus)) {
            System.out.println("✓ Order status updated to: " + newStatus);
        } else {
            System.out.println("✗ Failed to update order status.");
        }
    }
    
    private void viewSalesReport() {
        System.out.println("\n--- SALES REPORT ---");
        
        List<Order> allOrders = orderService.getAllOrders();
        double totalSales = orderService.getTotalSales();
        int pendingOrders = orderService.getPendingOrdersCount();
        
        System.out.println("Total Orders: " + allOrders.size());
        System.out.println("Total Sales: $" + String.format("%.2f", totalSales));
        System.out.println("Pending Orders: " + pendingOrders);
        
        System.out.println("\n--- Recent Orders ---");
        List<Order> recentOrders = orderService.getRecentOrders(5);
        for (Order order : recentOrders) {
            System.out.println(order);
        }
    }
    
    private void viewCustomers() {
        System.out.println("\n--- REGISTERED CUSTOMERS ---");
        List<Customer> customers = userService.getAllCustomers();
        
        if (customers.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        
        System.out.println(String.format("%-10s %-20s %-25s %-15s",
                          "ID", "Username", "Email", "Phone"));
        System.out.println("-".repeat(75));
        
        for (Customer customer : customers) {
            System.out.println(String.format("%-10s %-20s %-25s %-15s",
                              customer.getUserId(),
                              truncate(customer.getUsername(), 20),
                              truncate(customer.getEmail(), 25),
                              customer.getPhoneNumber()));
        }
    }
    
    // Helper methods
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
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
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
