package Thread_Programming_project.Threads;

import Thread_Programming_project.Methods;
import Thread_Programming_project.Product;
import Thread_Programming_project.Warehouse;

import java.util.*;

public class PriceLimitMonitor implements Runnable {

    private final Warehouse warehouse;

    private static final String PRICE_LIMITS_PATH =
            "src/Thread_Programming_project/Files/price_limits.bin";

    private HashMap<String, Double> priceLimits;
    private volatile boolean running = true;

    public PriceLimitMonitor(Warehouse warehouse) {

        this.warehouse = warehouse;
        this.priceLimits = Methods.getFile(PRICE_LIMITS_PATH);

        if (priceLimits == null) {
            priceLimits = new HashMap<>();
        }
    }

    public void setPriceLimit(Scanner input) {

        LinkedHashMap<String, Integer> myProducts = warehouse.getMyProductsSnapshot();
        TreeMap<String, Product> products = warehouse.getProductsSnapshot();

        String productId;

        while (true) {

            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (!myProducts.containsKey(productId)) {
                System.out.println("You do not own this product.");
                continue;
            }

            if (!products.containsKey(productId)) {
                System.out.println("Product does not exist.");
                continue;
            }

            break;
        }

        Product product = products.get(productId);

        System.out.println("Product: " + product.getName());
        System.out.println("Current price: " + product.getPrice());

        double limit;

        while (true) {

            System.out.print("Enter sell price limit (-1 to go back): ");

            try {

                limit = Double.parseDouble(input.nextLine().trim());

                if (limit == -1) {
                    return;
                }

                if (limit <= 0) {
                    System.out.println("Price limit must be greater than 0.");
                    continue;
                }

                if (limit >= product.getPrice()) {
                    System.out.println("Price limit must be lower than the current price: "
                            + product.getPrice());
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Price limit must be a valid number.");
            }
        }

        synchronized (priceLimits) {
            priceLimits.put(productId, limit);
            Methods.safeFile(PRICE_LIMITS_PATH, priceLimits);
        }

        System.out.println("Price limit activated.");
        System.out.println(product.getName()
                + " will be automatically sold at "
                + limit + " or lower.");
    }

    public void removePriceLimit(String productId) {

        synchronized (priceLimits) {
            priceLimits.remove(productId);
            Methods.safeFile(PRICE_LIMITS_PATH, priceLimits);
        }
    }

    @Override
    public void run() {

        while (running) {

            LinkedHashMap<String, Integer> myProducts = warehouse.getMyProductsSnapshot();
            TreeMap<String, Product> products = warehouse.getProductsSnapshot();

            HashMap<String, Double> limitsSnapshot;

            synchronized (priceLimits) {
                limitsSnapshot = new HashMap<>(priceLimits);
            }

            for (Map.Entry<String, Double> entry : limitsSnapshot.entrySet()) {

                String productId = entry.getKey();
                double limit = entry.getValue();

                if (!myProducts.containsKey(productId)) {
                    removePriceLimit(productId);
                    continue;
                }

                Product product = products.get(productId);

                if (product == null) {
                    removePriceLimit(productId);
                    continue;
                }

                if (product.getPrice() <= limit) {

                    boolean sold = warehouse.autoSellProduct(productId);

                    if (sold) {

                        removePriceLimit(productId);

                        System.out.println("[PRICE LIMIT] "
                                + product.getName()
                                + " reached "
                                + product.getPrice()
                                + " <= "
                                + limit);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void stop() {
        running = false;
    }
}