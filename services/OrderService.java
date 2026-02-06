package services;

import models.Order;
import models.Customer;
import models.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private List<Order> orders;
    private ProductService productService;
    private UserService userService; 

    public OrderService(ProductService productService, UserService userService) {
        this.orders = new ArrayList<>();
        this.productService = productService;
        this.userService = userService;
        loadOrders();
    }

    // Load orders from file
    public void loadOrders() {
        orders.clear();
        List<String> lines = FileManager.readLines(FileManager.getOrdersFile());
        for (String line : lines) {
            Order order = Order.fromCSV(line, productService);
            if (order != null) {
                orders.add(order);
            }
        }
    }

    // Save all orders
    public void saveOrders() {
        List<String> lines = new ArrayList<>();
        for (Order order : orders) {
            lines.add(order.toCSV());
        }
        FileManager.writeLines(FileManager.getOrdersFile(), lines);
    }

    //Place a new order WITH balance check
    public String placeOrder(Customer customer,
                             List<CartItem> cartItems,
                             String shippingAddress) {

        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty!");
            return null;
        }

        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }

        //Balance check
        if (customer.getBalance() < totalAmount) {
            System.out.println("Insufficient balance!");
            return null;
        }

        String orderId = generateNextOrderId();
        Order order = new Order(orderId, customer.getUserId(), shippingAddress);

        //Stock validation
        for (CartItem item : cartItems) {
            if (item.getProduct().getQuantity() < item.getQuantity()) {
                System.out.println("Not enough stock for: " + item.getProduct().getName());
                return null;
            }
        }

        //Commit order + stock
        for (CartItem item : cartItems) {
            order.addItem(item);
            item.getProduct().decreaseQuantity(item.getQuantity());
        }

        customer.setBalance(customer.getBalance() - totalAmount);

        orders.add(order);
        saveOrders();

        productService.saveProducts();
        customer.addOrder(orderId);
        userService.saveUsers(); 

        return orderId;
    }

    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        return orders.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByStatus(String status) {
        return orders.stream()
                .filter(o -> o.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public boolean updateOrderStatus(String orderId, String newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            saveOrders();
            return true;
        }
        return false;
    }

    public boolean cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order != null && order.getStatus().equals("PENDING")) {

            //restore stock
            for (CartItem item : order.getItems()) {
                item.getProduct().increaseQuantity(item.getQuantity());
            }
            productService.saveProducts();

            order.setStatus("CANCELLED");
            saveOrders();
            return true;
        }
        return false;
    }

    public double getTotalSales() {
        return orders.stream()
                .filter(o -> !o.getStatus().equals("CANCELLED"))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public List<Order> getRecentOrders(int count) {
        if (count >= orders.size()) {
            return new ArrayList<>(orders);
        }
        return orders.subList(Math.max(0, orders.size() - count), orders.size());
    }

    public String generateNextOrderId() {
        int maxId = 0;
        for (Order order : orders) {
            try {
                String idNum = order.getOrderId().replaceAll("[^0-9]", "");
                int id = Integer.parseInt(idNum);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return "ORD" + String.format("%05d", maxId + 1);
    }

    public int getPendingOrdersCount() {
        return (int) orders.stream()
                .filter(o -> o.getStatus().equals("PENDING"))
                .count();
    }
}
