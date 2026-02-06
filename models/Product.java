package models;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String productId;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String description;
    
    public Product() {
    }
    
    public Product(String productId, String name, String category, double price, int quantity, String description) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }
    
    // Getters and Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public boolean isInStock() {
        return quantity > 0;
    }
    
    public void decreaseQuantity(int amount) {
        if (amount > 0 && quantity >= amount) {
            quantity -= amount;
        }
    }
    
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Category: %s | Price: %.2f | Qty: %d | Desc: %s",
                productId, name, category, price, quantity, description);
    }
    
    // Convert format 
    public String toCSV() {
        return String.format("%s,%s,%s,%.2f,%d,%s",
                productId, name, category, price, quantity, description);
    }
    
    // Create Product
    public static Product fromCSV(String csv) {
        String[] parts = csv.split(",", 6);
        if (parts.length == 6) {
            return new Product(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                Double.parseDouble(parts[3].trim()),
                Integer.parseInt(parts[4].trim()),
                parts[5].trim()
            );
        }
        return null;
    }
}
