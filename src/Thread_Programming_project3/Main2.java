package Thread_Programming_project3;

import Thread_Programming_project3.Threads.MarketUpdater;
import Thread_Programming_project3.Threads.PortfolioUpdater;
import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;
import Thread_Programming_project3.system.Portfolio;

import java.util.HashMap;
import java.util.Map;

public class Main2 {

    public static void main(String[] args) throws InterruptedException {

        // 1. Δημιουργία αγοράς
        Market market = new Market(0.01, 0.012);

        // 2. Δημιουργία coins
        Coin btc = new Coin("BTC", "Bitcoin", 100.0, 0.015);
        Coin eth = new Coin("ETH", "Ethereum", 50.0, 0.020);
        Coin sol = new Coin("SOL", "Solana", 20.0, 0.025);

        market.addCoin(btc);
        market.addCoin(eth);
        market.addCoin(sol);

        // 3. Δημιουργία δοκιμαστικού Portfolio
        Portfolio portfolio = new Portfolio(
                "Test Portfolio",
                "TEST_ACCOUNT",
                1000.0
        );

        // 4. Δημιουργία Threads
        Thread marketThread = new Thread(
                new MarketUpdater(market),
                "MarketUpdater"
        );

        Thread portfolioThread = new Thread(
                new PortfolioUpdater(portfolio, market),
                "PortfolioUpdater"
        );

        // 5. Εκκίνηση
        marketThread.start();
        portfolioThread.start();

        System.out.println("Simulation started!");

        // 6. Παρακολούθηση για 30 δευτερόλεπτα
        for (int i = 1; i <= 10; i++) {

            Thread.sleep(3000);

            synchronized (market) {
                synchronized (portfolio) {

                    HashMap<String, Double> assets =
                            portfolio.getAssets();

                    double balance = portfolio.getAvailableBalance();
                    double totalValue = balance;

                    for (Map.Entry<String, Double> entry
                            : assets.entrySet()) {

                        Coin coin = market.findCoin(entry.getKey());

                        totalValue += entry.getValue()
                                * coin.getCurrentPrice();
                    }

                    System.out.println("\n=== TICK " + i + " ===");

                    System.out.printf("BTC: %.2f%n", btc.getCurrentPrice());
                    System.out.printf("ETH: %.2f%n", eth.getCurrentPrice());
                    System.out.printf("SOL: %.2f%n", sol.getCurrentPrice());

                    System.out.printf("Cash: %.2f%n", balance);
                    System.out.printf("Total Value: %.2f%n", totalValue);

                    System.out.println("Assets: " + assets);
                }
            }
        }

        // 7. Τερματισμός Threads
        marketThread.interrupt();
        portfolioThread.interrupt();

        marketThread.join();
        portfolioThread.join();

        System.out.println("\nSimulation finished!");
    }
}