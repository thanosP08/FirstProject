
package Thread_Programming_project3;

import Thread_Programming_project3.Threads.MarketUpdater;
import Thread_Programming_project3.Threads.PortfolioUpdater;
import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;
import Thread_Programming_project3.system.Portfolio;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {

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
