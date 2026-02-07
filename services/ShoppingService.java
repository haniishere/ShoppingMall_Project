package services;

import models.*;
import repositories.*;
import java.util.*;

public class ShoppingService {
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private User currentUser;
    private List<CartItem> cart;
    private int orderCounter;
    
    public ShoppingService() {
        this.productRepository = new ProductRepository();
        this.userRepository = new UserRepository();
        this.cart = new ArrayList<>();
        this.orderCounter = 1;
    }
    
    //Login
    public boolean login(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.currentUser = null;
        this.cart.clear();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isBuyer() {
        return currentUser != null && currentUser.getType() == User.UserType.BUYER;
    }
    
    public boolean isSeller() {
        return currentUser != null && currentUser.getType() == User.UserType.SELLER;
    }
    
    //Product methods
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }
    
    public void addProduct(Product product) {
        if (isSeller()) {
            productRepository.addProduct(product);
        }
    }
    
    public void updateProduct(Product product) {
        if (isSeller()) {
            productRepository.updateProduct(product);
        }
    }
    
    public void deleteProduct(int productId) {
        if (isSeller()) {
            productRepository.deleteProduct(productId);
        }
    }
    
    //Shopping cart methods
    public void addToCart(int productId, int quantity) {
        if (!isBuyer()) return;
        
        Product product = productRepository.getProductById(productId);
        if (product != null && product.getStockQuantity() >= quantity) {
            CartItem existingItem = cart.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                cart.add(new CartItem(product, quantity));
            }
        }
    }
    
    public void removeFromCart(int productId) {
        cart.removeIf(item -> item.getProduct().getId() == productId);
    }
    
    public void updateCartQuantity(int productId, int quantity) {
        CartItem item = cart.stream()
            .filter(i -> i.getProduct().getId() == productId)
            .findFirst()
            .orElse(null);
        
        if (item != null) {
            if (quantity <= 0) {
                removeFromCart(productId);
            } else {
                item.setQuantity(quantity);
            }
        }
    }
    
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cart);
    }
    
    public double getCartTotal() {
        return cart.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
    
    public void clearCart() {
        cart.clear();
    }
    
    //Order methods
    public Order checkout() {
        if (!isBuyer() || cart.isEmpty()) return null;
        
        double total = getCartTotal();
        if (currentUser.getBalance() >= total) {
            for (CartItem item : cart) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.updateProduct(product);
            }
            currentUser.deductBalance(total);
            userRepository.updateUser(currentUser);
            Order order = new Order(orderCounter++, currentUser.getUsername(), 
                                  new ArrayList<>(cart), total);
            
            clearCart();
            return order;
        }
        return null;
    }
    
    //User methods
    public boolean registerUser(String username, String password, User.UserType type) {
        if (userRepository.getUserByUsername(username) != null) {
            return false;
        }
        
        double initialBalance = type == User.UserType.BUYER ? 500.00 : 0.00;
        User newUser = new User(username, password, type, initialBalance);
        userRepository.addUser(newUser);
        return true;
    }
    
    public void updateUserBalance(String username, double amount) {
        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            user.setBalance(amount);
            userRepository.updateUser(user);
        }
    }
}