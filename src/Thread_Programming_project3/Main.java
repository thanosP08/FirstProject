
package Thread_Programming_project3;

import Thread_Programming_project3.Threads.MarketUpdater;
import Thread_Programming_project3.Threads.PortfolioUpdater;
import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;
import Thread_Programming_project3.system.Portfolio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {


    private static void showHistoricalInstructions(String csvFilePath) {

        System.out.println("\n========== HISTORICAL SIMULATION INSTRUCTIONS ==========");

        System.out.println("\n1. CSV FILE");
        System.out.println("Open the following CSV file:");
        System.out.println(csvFilePath);
        System.out.println("Enter the historical price data for your registered coins.");

        System.out.println("\n2. FILE STRUCTURE");
        System.out.println("The first row must contain the coin symbols followed by 'time'.");
        System.out.println("Example:");
        System.out.println("BTC,ETH,time");
        System.out.println("65000,3200,00:00:00");
        System.out.println("NULL,3210,00:00:05");
        System.out.println("65200,NULL,00:00:10");

        System.out.println("\n3. COIN REQUIREMENTS");
        System.out.println("- Use only coins registered in your Market.");
        System.out.println("- Each coin must appear exactly once in the header.");
        System.out.println("- Every coin must have at least one valid price.");
        System.out.println("- Do not change the order of the columns.");

        System.out.println("\n4. PRICE REQUIREMENTS");
        System.out.println("- Prices must be positive numbers.");
        System.out.println("- Negative prices, zero, NaN and Infinity are not allowed.");
        System.out.println("- Use NULL when a coin has no price update.");
        System.out.println("- Use a dot (.) for decimal values.");

        System.out.println("\n5. TIME REQUIREMENTS");
        System.out.println("- Time format: HH:MM:SS.");
        System.out.println("- Allowed range: 00:00:00 to 23:59:59.");
        System.out.println("- Timestamps must be strictly increasing.");
        System.out.println("- Duplicate timestamps are not allowed.");

        System.out.println("\n6. IMPORTANT RULES");
        System.out.println("- Separate all values using commas.");
        System.out.println("- Do not add extra commas or missing columns.");
        System.out.println("- Do not add extra text or comments to the CSV.");
        System.out.println("- Save your changes before starting the simulation.");

        System.out.println("\n7. STARTING THE SIMULATION");
        System.out.println("- Return to the Historical Simulation menu.");
        System.out.println("- Select option 4 to validate your CSV file.");
        System.out.println("- Select option 5 to start the simulation.");

        System.out.println("\n========================================================");
    }


    private static void createHistoricalCSV(
            Market market,
            Path path,
            boolean overwrite
    ) throws IOException {

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        ArrayList<String> symbols =
                new ArrayList<>(market.getCoins().keySet());

        Collections.sort(symbols);

        symbols.add("time");

        try (BufferedWriter writer = overwrite
                ? Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )
                : Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW
        )) {

            writer.write(String.join(",", symbols));
            writer.newLine();
        }
    }



    private static void historicalSimulationMenu(
            Market market,
            Portfolio portfolio,
            String csvFilePathForHistoryPrices
    ) throws InterruptedException {

        Path csvPath = Path.of(csvFilePathForHistoryPrices);

        while (true) {

            System.out.println("\n========== HISTORICAL SIMULATION ==========");
            System.out.println("1. Activate Historical Mode");
            System.out.println("2. Show Instructions");
            System.out.println("3. Historical Data Examples");
            System.out.println("4. Validate CSV File");
            System.out.println("5. Start Simulation");
            System.out.println("6. Reset Historical Data");
            System.out.println("0. Back to Main Menu");

            String choice = readInput("Choose: ");

            if (choice.equals("1")) {

                if (Files.exists(csvPath)) {
                    System.out.println("Historical mode is already activated.");
                    System.out.println("Your existing CSV file has been preserved.");
                    System.out.println("File: " + csvPath.toAbsolutePath());
                    showHistoricalInstructions(csvFilePathForHistoryPrices);
                    continue;
                }

                try {
                    createHistoricalCSV(market, csvPath, false);

                    System.out.println("Historical mode activated successfully!");
                    System.out.println("CSV file created: " + csvPath.toAbsolutePath());

                    showHistoricalInstructions(csvFilePathForHistoryPrices);

                } catch (IOException e) {
                    System.out.println("Failed to create CSV file: " + e.getMessage());
                }

            } else if (choice.equals("2")) {

                showHistoricalInstructions(csvFilePathForHistoryPrices);

            } else if (choice.equals("3")) {

                System.out.println("Historical data examples are not available yet.");

            } else if (choice.equals("4")) {

                System.out.println("CSV validation is not implemented yet.");

            } else if (choice.equals("5")) {

                System.out.println("Historical simulation is not implemented yet.");

            } else if (choice.equals("6")) {

                if (Files.exists(csvPath)) {
                    System.out.println("\nWARNING!");
                    System.out.println("All existing historical price data will be lost.");
                } else {
                    System.out.println("No existing CSV file was found.");
                    System.out.println("A new CSV template will be created.");
                }

                String confirmation = readInput(
                        "Do you want to continue? (YES/NO): "
                );

                if (!confirmation.equalsIgnoreCase("YES")) {
                    System.out.println("Reset cancelled.");
                    continue;
                }

                try {
                    createHistoricalCSV(market, csvPath, true);

                    System.out.println("Historical data reset successfully!");
                    System.out.println("New CSV template: " + csvPath.toAbsolutePath());

                    showHistoricalInstructions(csvFilePathForHistoryPrices);

                } catch (IOException e) {
                    System.out.println("Failed to reset CSV file: " + e.getMessage());
                }

            } else if (choice.equals("0")) {

                System.out.println("Returning to Main Menu...");
                return;

            } else {

                System.out.println("Invalid option!");
            }
        }
    }


    private static final BlockingQueue<String> INPUT =
            new LinkedBlockingQueue<>();

    private static final String EOF = "\u0000EOF";

    public static void main(String[] args)
            throws InterruptedException {

        startConsoleReader();

        Market market = null;
        Portfolio portfolio = null;

        while (true) {

            System.out.println("\n========== CRYPTO SIMULATOR ==========");
            System.out.println("1. Create Market");
            System.out.println("2. Add Coin");
            System.out.println("3. Show Market & Portfolio");
            System.out.println("4. Start Random Simulation");
            System.out.println("5. Start Historical Simulation");
            System.out.println("0. Exit");

            String choice = readInput("Choose: ");

            switch (choice) {

                case "1": {

                    if (market != null) {
                        System.out.println("Market already exists!");
                        break;
                    }

                    double trend = readDouble(
                            "Market Trend: ",
                            -Double.MAX_VALUE,
                            true
                    );

                    double volatility = readDouble(
                            "Market Volatility: ", 0, true
                    );

                    double capital = readDouble(
                            "Initial Capital: ", 0, false
                    );

                    market = new Market(trend, volatility);

                    // Δοκιμαστικό portfolio χωρίς Account
                    portfolio = new Portfolio(
                            "Demo Portfolio",
                            "DEMO",
                            capital
                    );

                    System.out.println("Market created!");
                    break;
                }

                case "2": {

                    if (market == null) {
                        System.out.println("Create Market first!");
                        break;
                    }

                    String symbol = readInput("Coin Symbol: ")
                            .toUpperCase(Locale.ROOT);

                    if (symbol.isBlank()) {
                        System.out.println("Invalid symbol!");
                        break;
                    }

                    if (market.findCoin(symbol) != null) {
                        System.out.println("Coin already exists!");
                        break;
                    }

                    String name = readInput("Coin Name: ");

                    if (name.isBlank()) {
                        System.out.println("Invalid name!");
                        break;
                    }

                    double price = readDouble(
                            "Initial Price: ", 0, false
                    );

                    double volatility = readDouble(
                            "Coin Volatility: ", 0, true
                    );

                    market.addCoin(new Coin(
                            symbol, name, price, volatility
                    ));

                    System.out.println("Coin added!");
                    break;
                }

                case "3": {

                    if (market == null) {
                        System.out.println("Create Market first!");
                        break;
                    }

                    showStatus(market, portfolio);
                    break;
                }

                case "4": {

                    if (market == null || market.getCoins().isEmpty()) {
                        System.out.println("Create a Market with coins first!");
                        break;
                    }

                    long duration = readDuration();

                    runRandomSimulation(
                            market,
                            portfolio,
                            duration
                    );

                    break;
                }

                case "5": {
                    System.out.println(
                            "Historical Simulation is not ready yet."
                    );
                    break;
                }

                case "0": {
                    System.out.println("Goodbye!");
                    return;
                }

                default: {
                    System.out.println("Invalid option!");
                }
            }
        }
    }

    // Διαβάζει την κονσόλα χωρίς να μπλοκάρει το Main
    private static void startConsoleReader() {

        Thread inputThread = new Thread(() -> {

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                INPUT.offer(scanner.nextLine().trim());
            }

            INPUT.offer(EOF);

        }, "ConsoleReader");

        inputThread.setDaemon(true);
        inputThread.start();
    }

    private static String readInput(String message)
            throws InterruptedException {

        System.out.print(message);

        String value = INPUT.take();

        if (value.equals(EOF)) {
            throw new IllegalStateException("Console input closed");
        }

        return value;
    }

    private static double readDouble(
            String message,
            double minimum,
            boolean inclusive)
            throws InterruptedException {

        while (true) {

            String input = readInput(message);

            try {

                double value = Double.parseDouble(input);

                boolean valid = Double.isFinite(value)
                        && (inclusive
                        ? value >= minimum
                        : value > minimum);

                if (valid) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // Ζητάμε ξανά αριθμό
            }

            System.out.println("Invalid number!");
        }
    }

    private static long readDuration()
            throws InterruptedException {

        while (true) {

            String input = readInput(
                    "Duration in seconds (0 = unlimited): "
            );

            try {

                long seconds = Long.parseLong(input);

                if (seconds >= 0 && seconds <= 86400) {
                    return seconds;
                }

            } catch (NumberFormatException e) {
                // Ζητάμε ξανά χρόνο
            }

            System.out.println(
                    "Enter a duration from 0 to 86400 seconds."
            );
        }
    }

    // Εκτέλεση της τυχαίας προσομοίωσης
    private static void runRandomSimulation(
            Market market,
            Portfolio portfolio,
            long durationSeconds)
            throws InterruptedException {

        Thread marketThread = new Thread(
                new MarketUpdater(market),
                "MarketUpdater"
        );

        Thread portfolioThread = new Thread(
                new PortfolioUpdater(portfolio, market),
                "PortfolioUpdater"
        );

        marketThread.start();
        portfolioThread.start();

        System.out.println("\nSimulation started!");
        System.out.println("Type STOP and press Enter to stop.");

        long startTime = System.nanoTime();
        long lastReport = startTime;

        long durationNanos =
                TimeUnit.SECONDS.toNanos(durationSeconds);

        try {

            while (true) {

                // Ελέγχουμε εντολές ανά 200 milliseconds
                String command = INPUT.poll(
                        200,
                        TimeUnit.MILLISECONDS
                );

                if (command != null) {

                    if (command.equalsIgnoreCase("STOP")
                            || command.equals(EOF)) {

                        System.out.println("Stop requested.");
                        break;
                    }

                    System.out.println(
                            "Type STOP to end the simulation."
                    );
                }

                long elapsed = System.nanoTime() - startTime;

                // Αυτόματη λήξη
                if (durationSeconds > 0
                        && elapsed >= durationNanos) {

                    System.out.println("Time limit reached!");
                    break;
                }

                // Έλεγχος αν κάποιο worker τερμάτισε
                if (!marketThread.isAlive()
                        || !portfolioThread.isAlive()) {

                    System.out.println(
                            "A simulation thread has stopped."
                    );
                    break;
                }

                // Εμφάνιση αποτελεσμάτων κάθε 3 sec
                if (System.nanoTime() - lastReport
                        >= TimeUnit.SECONDS.toNanos(3)) {

                    showStatus(market, portfolio);
                    lastReport = System.nanoTime();
                }
            }

        } finally {

            // Σταματάμε και τα δύο threads
            marketThread.interrupt();
            portfolioThread.interrupt();

            // Περιμένουμε να ολοκληρωθούν
            marketThread.join();
            portfolioThread.join();

            System.out.println("\nSimulation finished!");

            showStatus(market, portfolio);
        }
    }

    // Εμφάνιση τωρινής κατάστασης
    private static void showStatus(
            Market market,
            Portfolio portfolio) {

        synchronized (market) {
            synchronized (portfolio) {

                System.out.println("\n------ MARKET STATUS ------");

                System.out.println(
                        "Market Trend: " + market.getMarketTrend()
                );

                System.out.println(
                        "Market Volatility: " + market.getMarketVolatility()
                );

                for (Coin coin : market.getCoins().values()) {

                    System.out.printf(
                            Locale.US,
                            "%s: %.4f%n",
                            coin.getSymbol(),
                            coin.getCurrentPrice()
                    );
                }

                double totalValue = portfolio.getAvailableBalance();

                HashMap<String, Double> assets =
                        portfolio.getAssets();

                for (Map.Entry<String, Double> entry
                        : assets.entrySet()) {

                    Coin coin = market.findCoin(entry.getKey());

                    if (coin != null) {
                        totalValue += entry.getValue()
                                * coin.getCurrentPrice();
                    }
                }

                System.out.printf(
                        Locale.US,
                        "Cash: %.2f%n",
                        portfolio.getAvailableBalance()
                );

                System.out.printf(
                        Locale.US,
                        "Portfolio Value: %.2f%n",
                        totalValue
                );

                System.out.println("Assets: " + assets);
                System.out.println("---------------------------");
            }
        }
    }
}
