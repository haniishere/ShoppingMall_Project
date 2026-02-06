package models;

public class CartItem {
    private Product product;
    private int quantity;
    
    // Constructors
    public CartItem() {
    }
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // Business methods
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }
    
    public void decreaseQuantity(int amount) {
        if (amount > 0 && quantity >= amount) {
            quantity -= amount;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s | Quantity: %d | Subtotal: %.2f",
                product.getName(), quantity, getTotalPrice());
    }
}
