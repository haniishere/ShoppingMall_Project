import ui.LoginUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("========================================");
                System.out.println("   SHOPPING MALL MANAGEMENT SYSTEM      ");
                System.out.println("========================================");
                System.out.println("Starting application...");
                try {
                    new LoginUI();
                    System.out.println("Application started successfully!");
                    System.out.println("Default login credentials:");
                    System.out.println("- Buyer: buyer1 / pass123 (Balance: $1000)");
                    System.out.println("- Buyer: buyer2 / pass123 (Balance: $500)");
                    System.out.println("- Seller: seller1 / pass123");
                    System.out.println("========================================");
                } catch (Exception e) {
                    System.err.println("Error starting application: " + e.getMessage());
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}