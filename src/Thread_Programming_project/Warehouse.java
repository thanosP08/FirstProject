package Thread_Programming_project;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Warehouse {

    private static final int AUTO_RESTOCK_AMOUNT = 5;

    private TreeMap<String, Product> products;
    private LinkedHashMap<String, Integer> myProducts;

    private final String productsPath;
    private final String myProductsPath;

    public Warehouse(String productsPath, String myProductsPath) {

        this.productsPath = productsPath;
        this.myProductsPath = myProductsPath;

        products = Methods.getFile(productsPath);
        myProducts = Methods.getFile(myProductsPath);

        if (products == null) {
            products = new TreeMap<>();
        }

        if (myProducts == null) {
            myProducts = new LinkedHashMap<>();
        }
    }

    public synchronized void createProduct(Product product) {

        if (product == null) {
            return;
        }

        products.put(product.getId(), product);
        saveProducts();

        System.out.println("Product created successfully.");
        System.out.println("ID: " + product.getId());
    }

    public synchronized void addProduct(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                System.out.println("Product found.");
                break;
            }

            System.out.println("Invalid product ID.");
        }

        Product product = products.get(productId);
        int quantity;

        while (true) {
            System.out.print("Enter quantity (Available: " + product.getQuantity() + ", -1 to go back): ");

            try {
                quantity = Integer.parseInt(input.nextLine().trim());

                if (quantity == -1) {
                    return;
                }

                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than 0.");
                    continue;
                }

                if (quantity > product.getQuantity()) {
                    System.out.println("Not enough stock. Available: " + product.getQuantity());
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Quantity must be an integer.");
            }
        }

        if (!Methods.Payment()) {
            System.out.println("Payment failed.");
            return;
        }

        myProducts.put(productId, myProducts.getOrDefault(productId, 0) + quantity);
        product.setQuantity(product.getQuantity() - quantity);

        saveProducts();
        saveMyProducts();

        System.out.println("Purchase completed: " + product.getName() + " x" + quantity);
    }

    public synchronized void sellProduct(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID to sell (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId) && myProducts.containsKey(productId)) {
                System.out.println("Product found.");
                break;
            }

            System.out.println("You do not own this product.");
        }

        Product product = products.get(productId);
        int quantity = myProducts.get(productId);

        System.out.println("ID: " + product.getId()
                + " | Name: " + product.getName()
                + " | Quantity: " + quantity
                + " | Price: " + product.getPrice()
                + " | Total: " + product.getPrice() * quantity);

        int sellQuantity;

        while (true) {
            System.out.print("Enter quantity to sell (You own: " + quantity + ", -1 to go back): ");

            try {
                sellQuantity = Integer.parseInt(input.nextLine().trim());

                if (sellQuantity == -1) {
                    return;
                }

                if (sellQuantity <= 0) {
                    System.out.println("Quantity must be greater than 0.");
                    continue;
                }

                if (sellQuantity > quantity) {
                    System.out.println("Maximum quantity: " + quantity);
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Quantity must be an integer.");
            }
        }

        int remainingQuantity = quantity - sellQuantity;

        if (remainingQuantity == 0) {
            myProducts.remove(productId);
        } else {
            myProducts.put(productId, remainingQuantity);
        }

        product.setQuantity(product.getQuantity() + sellQuantity);

        saveProducts();
        saveMyProducts();

        System.out.println("Sale completed: " + product.getName() + " x" + sellQuantity);
    }

    public synchronized void restockProduct(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                System.out.println("Product found.");
                break;
            }

            System.out.println("Invalid product ID.");
        }

        Product product = products.get(productId);

        System.out.println("Current quantity: " + product.getQuantity());

        int restockQuantity;

        while (true) {
            System.out.print("Enter restock quantity (-1 to go back): ");

            try {
                restockQuantity = Integer.parseInt(input.nextLine().trim());

                if (restockQuantity == -1) {
                    return;
                }

                if (restockQuantity <= 0) {
                    System.out.println("Restock quantity must be greater than 0.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Restock quantity must be an integer.");
            }
        }

        product.setQuantity(product.getQuantity() + restockQuantity);
        saveProducts();

        System.out.println("Restock completed: " + product.getName()
                + " | New quantity: " + product.getQuantity());
    }

    public synchronized void changePrice(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                System.out.println("Product found.");
                break;
            }

            System.out.println("Invalid product ID.");
        }

        Product product = products.get(productId);

        System.out.println("Current price: " + product.getPrice());

        double newPrice;

        while (true) {
            System.out.print("Enter new price (-1 to go back): ");

            try {
                newPrice = Double.parseDouble(input.nextLine().trim());

                if (newPrice == -1) {
                    return;
                }

                if (newPrice <= 0) {
                    System.out.println("Price must be greater than 0.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Price must be a valid number.");
            }
        }

        product.setPrice(newPrice);
        saveProducts();

        System.out.println("Price changed: " + product.getName()
                + " | New price: " + newPrice);
    }

    public synchronized void changeName(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                break;
            }

            System.out.println("Invalid product ID.");
        }

        String newName;

        while (true) {
            System.out.print("Enter new name (or 'exit' to go back): ");
            newName = input.nextLine().trim();

            if (newName.equalsIgnoreCase("exit")) {
                return;
            }

            if (!newName.matches("[\\p{L}_]+")) {
                System.out.println("Use only letters and _");
                continue;
            }

            break;
        }

        Product product = products.get(productId);
        String oldName = product.getName();

        product.setName(newName);
        saveProducts();

        System.out.println("Name changed: " + oldName + " -> " + newName);
    }

    public synchronized void changeMinimumQuantity(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                break;
            }

            System.out.println("Invalid product ID.");
        }

        Product product = products.get(productId);
        int newMinimum;

        while (true) {
            System.out.print("Enter new minimum quantity (-1 to go back): ");

            try {
                newMinimum = Integer.parseInt(input.nextLine().trim());

                if (newMinimum == -1) {
                    return;
                }

                if (newMinimum < 0) {
                    System.out.println("Minimum quantity cannot be negative.");
                    continue;
                }

                if (newMinimum >= product.getQuantity()) {
                    System.out.println("Minimum must be smaller than current stock: "
                            + product.getQuantity());
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Minimum quantity must be an integer.");
            }
        }

        product.setMinimumQuantity(newMinimum);
        saveProducts();

        System.out.println("Minimum quantity changed to: " + newMinimum);
    }

    public synchronized void deleteProduct(Scanner input) {

        String productId;

        while (true) {
            System.out.print("Enter product ID to delete (or 'exit' to go back): ");
            productId = input.nextLine().trim();

            if (productId.equalsIgnoreCase("exit")) {
                return;
            }

            if (products.containsKey(productId)) {
                break;
            }

            System.out.println("Invalid product ID.");
        }

        if (myProducts.containsKey(productId)) {
            System.out.println("Product cannot be deleted because you currently own it.");
            return;
        }

        Product product = products.get(productId);

        System.out.print("Delete " + product.getName() + "? (yes/no): ");
        String answer = input.nextLine().trim();

        if (!answer.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        products.remove(productId);
        saveProducts();

        System.out.println("Product deleted successfully.");
    }

    public synchronized void showProducts() {

        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        int cnt = 1;

        for (Product product : products.values()) {
            System.out.println("Product " + cnt
                    + " { ID: " + product.getId()
                    + ", Name: " + product.getName()
                    + ", Quantity: " + product.getQuantity()
                    + ", Minimum: " + product.getMinimumQuantity()
                    + ", Price: " + product.getPrice()
                    + " }");

            cnt++;
        }
    }

    public synchronized void showMyProducts() {

        if (myProducts.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        int cnt = 1;

        for (Map.Entry<String, Integer> entry : myProducts.entrySet()) {

            Product product = products.get(entry.getKey());

            if (product == null) {
                continue;
            }

            int quantity = entry.getValue();

            System.out.println("Product " + cnt
                    + " { ID: " + product.getId()
                    + ", Name: " + product.getName()
                    + ", Quantity: " + quantity
                    + ", Price: " + product.getPrice()
                    + ", Total: " + product.getPrice() * quantity
                    + " }");

            cnt++;
        }
    }

    public synchronized void checkLowStock() {

        if (products.isEmpty()) {
            return;
        }

        boolean changed = false;

        for (Product product : products.values()) {

            if (product.getQuantity() <= product.getMinimumQuantity()) {

                int oldQuantity = product.getQuantity();
                product.setQuantity(product.getQuantity() + AUTO_RESTOCK_AMOUNT);

                System.out.println("[AUTO RESTOCK] " + product.getName()
                        + " | " + oldQuantity + " -> " + product.getQuantity());

                changed = true;
            }
        }

        if (changed) {
            saveProducts();
        }
    }

    public synchronized boolean autoSellProduct(String productId) {

        if (!myProducts.containsKey(productId)) {
            return false;
        }

        Product product = products.get(productId);

        if (product == null) {
            return false;
        }

        int quantity = myProducts.get(productId);

        myProducts.remove(productId);
        product.setQuantity(product.getQuantity() + quantity);

        saveProducts();
        saveMyProducts();

        System.out.println("[AUTO SELL] " + product.getName()
                + " x" + quantity
                + " | Price: " + product.getPrice());

        return true;
    }

    private synchronized void saveProducts() {
        Methods.safeFile(productsPath, products);
    }

    private synchronized void saveMyProducts() {
        Methods.safeFile(myProductsPath, myProducts);
    }

    public synchronized TreeMap<String, Product> getProductsSnapshot() {
        return new TreeMap<>(products);
    }

    public synchronized LinkedHashMap<String, Integer> getMyProductsSnapshot() {
        return new LinkedHashMap<>(myProducts);
    }
}