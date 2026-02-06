package models;

import java.util.ArrayList;
import java.util.List;


public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private String address;
    private List<String> orderHistory;
    private double balance;

    // Constructors
    public Customer() {
        super();
        this.address = "";
        this.orderHistory = new ArrayList<>();
        this.balance = 0.0;
    }

    public Customer(String userId,
                    String username,
                    String password,
                    String email,
                    String phoneNumber,
                    String address) {

        super(userId, username, password, email, phoneNumber);
        this.address = address;
        this.orderHistory = new ArrayList<>();
        this.balance = 0.0;
    }

    public Customer(String userId,
                    String username,
                    String password,
                    String email,
                    String phoneNumber,
                    String address,
                    double balance) {

        super(userId, username, password, email, phoneNumber);
        this.address = address;
        this.orderHistory = new ArrayList<>();
        this.balance = balance;
    }

    // Getters & Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getOrderHistory() {
        return orderHistory;
    }

    public void addOrder(String orderId) {
        orderHistory.add(orderId);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String getUserType() {
        return "CUSTOMER";
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Address: " + address
                + " | Balance: " + balance;
    }

    public String toCSV() {
        String orders = String.join(";", orderHistory);
        return String.format(
                "CUSTOMER,%s,%s,%s,%s,%s,%s,%.2f,%s",
                userId,
                username,
                password,
                email,
                phoneNumber,
                address,
                balance,
                orders
        );
    }

    public static Customer fromCSV(String csv) {
        String[] parts = csv.split(",", 9);

        if (parts.length >= 7 && parts[0].equals("CUSTOMER")) {
            double balance = 0.0;
            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                balance = Double.parseDouble(parts[7].trim());
            }

            Customer customer = new Customer(
                    parts[1].trim(),  // userId
                    parts[2].trim(),  // username
                    parts[3].trim(),  // password
                    parts[4].trim(),  // email
                    parts[5].trim(),  // phone
                    parts[6].trim(),  // address
                    balance            // balance
            );

            if (parts.length == 9 && !parts[8].trim().isEmpty()) {
                String[] orders = parts[8].split(";");
                for (String order : orders) {
                    if (!order.trim().isEmpty()) {
                        customer.addOrder(order.trim());
                    }
                }
            }
            return customer;
        }
        return null;
    }
}
