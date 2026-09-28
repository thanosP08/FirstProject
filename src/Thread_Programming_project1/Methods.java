package Thread_Programming_project1;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public abstract class Methods {

    public static void mainMenu() {
        System.out.println("1. BUY PRODUCT");
        System.out.println("2. SELL PRODUCT");
        System.out.println("3. SHOW PRODUCTS");
        System.out.println("4. SHOW MY PRODUCTS");
        System.out.println("5. SET AUTO SELL PRICE");
        System.out.println("6. PRODUCT MANAGER");
        System.out.println("7. EXIT");
        System.out.print("Choice: ");
    }

    public static void managerMenu() {
        System.out.println("1. CREATE PRODUCT");
        System.out.println("2. RESTOCK PRODUCT");
        System.out.println("3. CHANGE PRICE");
        System.out.println("4. CHANGE NAME");
        System.out.println("5. CHANGE MINIMUM QUANTITY");
        System.out.println("6. DELETE PRODUCT");
        System.out.println("7. SHOW PRODUCTS");
        System.out.println("8. BACK");
        System.out.print("Choice: ");
    }

    public static <T extends Serializable> void safeFile(String filePath, T data) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(data);

        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (T) ois.readObject();

        } catch (Exception e) {
            return null;
        }
    }

    public static String idSet(String idFilePath) {

        HashSet<String> idSet = getFile(idFilePath);

        if (idSet == null) {
            idSet = new HashSet<>();
        }

        Random rand = new Random();

        while (true) {

            String idNumber = String.valueOf(rand.nextInt(9000) + 1000);

            if (!idSet.contains(idNumber)) {
                idSet.add(idNumber);
                safeFile(idFilePath, idSet);
                return idNumber;
            }
        }
    }

    public static Product createProduct(Scanner input) {

        String name;

        while (true) {
            System.out.print("Give name (or exit for menu): ");
            name = input.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                return null;
            }

            if (!name.matches("[\\p{L}_]+")) {
                System.out.println("Invalid name. Use only letters and _");
                continue;
            }

            break;
        }

        int quantity;

        while (true) {
            System.out.print("Give quantity > 0 (or -1 for menu): ");

            try {
                quantity = Integer.parseInt(input.nextLine().trim());

                if (quantity == -1) {
                    return null;
                }

                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than 0.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Quantity must be an integer.");
            }
        }

        int minimumQuantity;

        while (true) {
            System.out.print("Give minimum quantity (or -1 for menu): ");

            try {
                minimumQuantity = Integer.parseInt(input.nextLine().trim());

                if (minimumQuantity == -1) {
                    return null;
                }

                if (minimumQuantity < 0) {
                    System.out.println("Minimum quantity cannot be negative.");
                    continue;
                }

                if (minimumQuantity >= quantity) {
                    System.out.println("Minimum quantity must be smaller than current quantity.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Minimum quantity must be an integer.");
            }
        }

        double price;

        while (true) {
            System.out.print("Give price (or -1 for menu): ");

            try {
                price = Double.parseDouble(input.nextLine().trim());

                if (price == -1) {
                    return null;
                }

                if (price <= 0) {
                    System.out.println("Price must be greater than 0.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Price must be a valid number.");
            }
        }

        return new Product(name, quantity, minimumQuantity, price);
    }

    public static synchronized boolean Payment() {
        return true;
    }
}