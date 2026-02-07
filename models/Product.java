package models;

import java.util.Arrays;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String description;
    private Category category;
    private double price;
    private int stockQuantity;
    
    public enum Category {
        ELECTRONICS,
        CLOTHING,
        BOOKS,
        GROCERIES,
        FURNITURE,
        SPORTS,
        BEAUTY
    }
    
    public Product() {
    }
    
    public Product(int id, String name, String description, Category category, 
                  double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public static List<Category> getAllCategories() {
        return Arrays.asList(Category.values());
    }
    
    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}