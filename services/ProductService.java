package services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import models.Product;

public class ProductService {

    private List<Product> products;

    public ProductService() {
        this.products = new ArrayList<>();
        loadProducts();
    }

    // Load products from file
    public void loadProducts() {
        products.clear();
        List<String> lines = FileManager.readLines(FileManager.getProductsFile());

        for (String line : lines) {
            Product product = Product.fromCSV(line);
            if (product != null) {
                products.add(product);
            }
        }
    }

    // Save all products to file
    public void saveProducts() {
        List<String> lines = new ArrayList<>();
        for (Product product : products) {
            lines.add(product.toCSV());
        }
        FileManager.writeLines(FileManager.getProductsFile(), lines);
    }

    // Add a new product
    public boolean addProduct(Product product) {
        if (getProductById(product.getProductId()) != null) {
            System.out.println("Product with ID " + product.getProductId() + " already exists!");
            return false;
        }
        products.add(product);
        saveProducts();
        return true;
    }

    // Update an existing product
    public boolean updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(updatedProduct.getProductId())) {
                products.set(i, updatedProduct);
                saveProducts();
                return true;
            }
        }
        return false;
    }

    // Delete a product
    public boolean deleteProduct(String productId) {
        Product product = getProductById(productId);
        if (product != null) {
            products.remove(product);
            saveProducts();
            return true;
        }
        return false;
    }

    // Get product by ID
    public Product getProductById(String productId) {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // Search products by name
    public List<Product> searchProductsByName(String searchTerm) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }


    // Sort by name
    public List<Product> sortByName(boolean ascending) {
        return products.stream()
                .sorted(ascending
                        ? Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                        : Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed())
                .collect(Collectors.toList());
    }

    // Sort by category
    public List<Product> sortByCategory(boolean ascending) {
        return products.stream()
                .sorted(ascending
                        ? Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER)
                        : Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER).reversed())
                .collect(Collectors.toList());
    }

    // Sort by price
    public List<Product> sortByPrice(boolean ascending) {
        return products.stream()
                .sorted(ascending
                        ? Comparator.comparingDouble(Product::getPrice)
                        : Comparator.comparingDouble(Product::getPrice).reversed())
                .collect(Collectors.toList());
    }

    // Stock-related helpers
    public List<Product> getInStockProducts() {
        return products.stream()
                .filter(Product::isInStock)
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts() {
        return products.stream()
                .filter(p -> p.getQuantity() > 0 && p.getQuantity() < 10)
                .collect(Collectors.toList());
    }

    public List<Product> getOutOfStockProducts() {
        return products.stream()
                .filter(p -> p.getQuantity() == 0)
                .collect(Collectors.toList());
    }

    // Update product quantity
    public boolean updateProductQuantity(String productId, int newQuantity) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setQuantity(newQuantity);
            saveProducts();
            return true;
        }
        return false;
    }

    // Get all unique categories
    public List<String> getAllCategories() {
        return products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    // Generate next product ID
    public String generateNextProductId() {
        int maxId = 0;
        for (Product product : products) {
            try {
                String idNum = product.getProductId().replaceAll("[^0-9]", "");
                int id = Integer.parseInt(idNum);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return "P" + String.format("%04d", maxId + 1);
    }
}
