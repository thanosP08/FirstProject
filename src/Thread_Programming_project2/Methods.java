package Thread_Programming_project2;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public abstract class Methods {

    public static <T extends Serializable> void safeFile(String filePath, T data) {
        File file = new File(filePath);
        File parentFolder = file.getParentFile();

        if (parentFolder != null && !parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file);
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

    public static void createResultFile(Coin[] coins, User[] users) {
        File resultFolder = new File("src/Thread_Programming_project2/Files/result");

        if (!resultFolder.exists()) {
            resultFolder.mkdirs();
        }

        File resultFile = new File(resultFolder, "result.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(resultFile))) {

            writer.println("================================");
            writer.println("      TRADING SIMULATION");
            writer.println("================================");
            writer.println();

            writer.println("GENERAL STATISTICS");
            writer.println("------------------");
            writer.println("Total Buys: " + Coin.getBuys());
            writer.println("Total Sells: " + Coin.getSells());
            writer.println("Total Trades: " + (Coin.getBuys() + Coin.getSells()));
            writer.println();

            writer.println("COINS");
            writer.println("-----");

            for (Coin coin : coins) {
                writer.println(coin.getSymbol() + " - " + coin.getName());
                writer.printf("Final Price: %.2f%n", coin.getPrice());
                writer.printf("Final Quantity: %.2f%n", coin.getQuantity());
                writer.println();
            }

            writer.println("USERS");
            writer.println("-----");

            for (User user : users) {
                writer.println(user.getName());
                writer.printf("Starting Money: %.2f%n", user.getStartingMoney());
                writer.printf("Final Money: %.2f%n", user.getMoney());
                writer.printf("Money Difference: %+.2f%n",
                        user.getMoney() - user.getStartingMoney());
                HashMap<String, Integer> userCoins = user.getUserCoins();
                writer.println("Coins:");

                if (userCoins.isEmpty()) {
                    writer.println("None");
                } else {
                    for (String coinId : userCoins.keySet()) {
                        String symbol = coinId;
                        for (Coin coin : coins) {
                            if (coin.getId().equals(coinId)) {
                                symbol = coin.getSymbol();
                                break;
                            }
                        }
                        writer.println(symbol + " -> " + userCoins.get(coinId));
                    }
                }
                writer.println();
            }
            writer.println("================================");
            writer.println("END OF SIMULATION");
            writer.println("================================");

            System.out.println("Result file created successfully.");

        } catch (IOException e) {
            System.out.println("Error creating result file.");
        }
    }
}