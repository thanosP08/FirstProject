package Thread_Programming_project;

import Thread_Programming_project.Threads.PriceLimitMonitor;
import Thread_Programming_project.Threads.StockMonitor;

import java.util.Scanner;

public class Main2 {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        String productsPath = "src/Thread_Programming_project/Files/products.bin";
        String myProductsPath = "src/Thread_Programming_project/Files/myproducts.bin";

        Warehouse warehouse = new Warehouse(productsPath, myProductsPath);

        StockMonitor stockMonitor = new StockMonitor(warehouse);
        Thread stockThread = new Thread(stockMonitor, "Stock-Monitor");

        PriceLimitMonitor priceLimitMonitor = new PriceLimitMonitor(warehouse);
        Thread priceLimitThread = new Thread(priceLimitMonitor, "Price-Limit-Monitor");

        stockThread.start();
        priceLimitThread.start();

        mainLoop:
        while (true) {

            Methods.mainMenu();
            String choice = input.nextLine().trim();

            switch (choice) {

                case "1":
                    warehouse.addProduct(input);
                    break;

                case "2":
                    warehouse.sellProduct(input);
                    break;

                case "3":
                    warehouse.showProducts();
                    break;

                case "4":
                    warehouse.showMyProducts();
                    break;

                case "5":
                    priceLimitMonitor.setPriceLimit(input);
                    break;

                case "6":

                    managerLoop:
                    while (true) {

                        Methods.managerMenu();
                        String managerChoice = input.nextLine().trim();

                        switch (managerChoice) {

                            case "1":
                                Product product = Methods.createProduct(input);

                                if (product != null) {
                                    warehouse.createProduct(product);
                                }
                                break;

                            case "2":
                                warehouse.restockProduct(input);
                                break;

                            case "3":
                                warehouse.changePrice(input);
                                break;

                            case "4":
                                warehouse.changeName(input);
                                break;

                            case "5":
                                warehouse.changeMinimumQuantity(input);
                                break;

                            case "6":
                                warehouse.deleteProduct(input);
                                break;

                            case "7":
                                warehouse.showProducts();
                                break;

                            case "8":
                                break managerLoop;

                            default:
                                System.out.println("Give a number from 1 to 8.");
                        }
                    }

                    break;

                case "7":
                    System.out.println("Closing program...");
                    break mainLoop;

                default:
                    System.out.println("Give a number from 1 to 7.");
            }
        }

        stockMonitor.stop();
        priceLimitMonitor.stop();

        stockThread.interrupt();
        priceLimitThread.interrupt();

        try {
            stockThread.join();
            priceLimitThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        input.close();
        System.out.println("Program closed.");
    }
}