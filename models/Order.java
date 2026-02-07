package models;

import java.util.Date;
import java.util.List;

public class Order {
    private int orderId;
    private String username;
    private List<CartItem> items;
    private double totalAmount;
    private Date orderDate;
    private OrderStatus status;
    
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }
    
    public Order(int orderId, String username, List<CartItem> items, double totalAmount) {
        this.orderId = orderId;
        this.username = username;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
    }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}