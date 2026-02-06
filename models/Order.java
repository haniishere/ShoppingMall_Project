package models;

import services.ProductService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String orderId;
    private String customerId;
    private List<CartItem> items;
    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private String shippingAddress;

    // Constructors
    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Order(String orderId, String customerId, String shippingAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.totalAmount = 0.0;
    }

    // Getters & Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    // Business logic
    public void addItem(CartItem item) {
        items.add(item);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (CartItem item : items) {
            totalAmount += item.getTotalPrice();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "Order ID: %s | Customer: %s | Total: %.2f | Status: %s | Date: %s",
                orderId, customerId, totalAmount, status, orderDate.format(formatter)
        );
    }

    // CSV
    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder itemsStr = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            itemsStr.append(item.getProduct().getProductId())
                    .append(":")
                    .append(item.getQuantity());
            if (i < items.size() - 1) {
                itemsStr.append(";");
            }
        }

        return String.format(
                "%s,%s,%.2f,%s,%s,%s,%s",
                orderId,
                customerId,
                totalAmount,
                status,
                orderDate.format(formatter),
                shippingAddress,
                itemsStr
        );
    }

    // Create Order from CSV
    public static Order fromCSV(String csv, ProductService productService) {
        String[] parts = csv.split(",", 7);
        if (parts.length != 7) return null;

        Order order = new Order();
        order.setOrderId(parts[0].trim());
        order.setCustomerId(parts[1].trim());
        order.totalAmount = Double.parseDouble(parts[2].trim());
        order.setStatus(parts[3].trim());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setOrderDate(LocalDateTime.parse(parts[4].trim(), formatter));
        order.setShippingAddress(parts[5].trim());

        if (!parts[6].trim().isEmpty()) {
            String[] itemParts = parts[6].split(";");
            for (String itemStr : itemParts) {
                String[] itemData = itemStr.split(":");
                if (itemData.length == 2) {
                    String productId = itemData[0].trim();
                    int quantity = Integer.parseInt(itemData[1].trim());

                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        order.addItem(new CartItem(product, quantity));
                    }
                }
            }
        }

        return order;
    }
}
