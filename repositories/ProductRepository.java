package repositories;

import java.io.*;
import java.util.*;
import models.Product;

public class ProductRepository {
    private static final String CSV_FILE = "products.csv";
    private static final String HEADER = "id,name,description,category,price,stock";
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length == 6) {
                    Product product = new Product(
                        Integer.parseInt(values[0]),
                        values[1],
                        values[2],
                        Product.Category.valueOf(values[3]),
                        Double.parseDouble(values[4]),
                        Integer.parseInt(values[5])
                    );
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("CSV file not found, creating new one: " + e.getMessage());
            initializeCSV();
        }
        
        return products;
    }
    
    public Product getProductById(int id) {
        return getAllProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void addProduct(Product product) {
        List<Product> products = getAllProducts();
        
        // Generate new ID
        int newId = products.isEmpty() ? 1 : 
                    products.stream().mapToInt(Product::getId).max().getAsInt() + 1;
        product.setId(newId);
        
        products.add(product);
        saveAllProducts(products);
    }
    
    public void updateProduct(Product updatedProduct) {
        List<Product> products = getAllProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                break;
            }
        }
        saveAllProducts(products);
    }
    
    public void deleteProduct(int id) {
        List<Product> products = getAllProducts();
        products.removeIf(p -> p.getId() == id);
        saveAllProducts(products);
    }
    
    private void saveAllProducts(List<Product> products) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println(HEADER);
            for (Product product : products) {
                pw.println(String.format("%d,%s,%s,%s,%.2f,%d",
                    product.getId(),
                    product.getName().replace(",", ";"),
                    product.getDescription().replace(",", ";"),
                    product.getCategory().name(),
                    product.getPrice(),
                    product.getStockQuantity()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println(HEADER);
            List<Product> sampleProducts = Arrays.asList(
                new Product(1, "Laptop", "Gaming laptop with RTX 4090", 
                           Product.Category.ELECTRONICS, 1999.99, 10),
                new Product(2, "TShirt", "Cotton T-Shirt", 
                           Product.Category.CLOTHING, 29.99, 50),
                new Product(3, "Shahnameh", "Learn Java programming", 
                           Product.Category.BOOKS, 49.99, 30),
                new Product(4, "Orange", "Fresh red apples", 
                           Product.Category.GROCERIES, 2.99, 100)
            );
            
            for (Product product : sampleProducts) {
                pw.println(String.format("%d,%s,%s,%s,%.2f,%d",
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getCategory().name(),
                    product.getPrice(),
                    product.getStockQuantity()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}