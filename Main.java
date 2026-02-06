import services.*;
import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {

        ProductService productService = new ProductService();
        UserService userService = new UserService();
        OrderService orderService = new OrderService(productService, userService);

        new LoginFrame(userService, productService, orderService);
    }
}
