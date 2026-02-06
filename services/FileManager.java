package services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.txt";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String ORDERS_FILE = DATA_DIR + "orders.txt";
    
    private static boolean initialized = false;
    
    // Private constructor to prevent instantiation
    private FileManager() {}
    
    // Initialize data directory and files
    public static void initialize() {
        if (initialized) {
            return; // Already initialized
        }
        
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("Created data directory: " + dataDir.getAbsolutePath());
            }
        }
        
        createFileIfNotExists(PRODUCTS_FILE);
        createFileIfNotExists(USERS_FILE);
        createFileIfNotExists(ORDERS_FILE);
        
        initialized = true;
        System.out.println("FileManager initialized successfully");
    }
    
    private static void createFileIfNotExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("Created file: " + filename);
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + filename);
                e.printStackTrace();
            }
        }
    }
    
    // Auto-initialize before any file operation
    private static void checkInitialized() {
        if (!initialized) {
            initialize();
        }
    }
    
    public static List<String> readLines(String filename) {
        checkInitialized();
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        
        return lines;
    }
    
    // Write lines to a file 
    public static void writeLines(String filename, List<String> lines) {
        checkInitialized();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filename);
            e.printStackTrace();
        }
    }
    
    // Append a line to a file
    public static void appendLine(String filename, String line) {
        checkInitialized();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filename);
            e.printStackTrace();
        }
    }
    
    // Get file paths (no need to initialize for these)
    public static String getProductsFile() {
        return PRODUCTS_FILE;
    }
    
    public static String getUsersFile() {
        return USERS_FILE;
    }
    
    public static String getOrdersFile() {
        return ORDERS_FILE;
    }
}