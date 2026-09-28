package Thread_Programming_project2;

import Thread_Programming_project2.Threads.PriceMaker;
import Thread_Programming_project2.Threads.TraderTask;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        User user1 = new User("James", 12000);
        User user2 = new User("Emily", 15000);
        User user3 = new User("Thanos", 25000);

        Coin coin1 = new Coin("BTC", "Bitcoin", 2500, 50);
        Coin coin2 = new Coin("ETH", "Ethereum", 1000, 100);
        Coin coin3 = new Coin("SOL", "Solana", 200, 500);

        TraderTask trader1 = new TraderTask(user1, coin1, 1, 1);
        TraderTask trader2 = new TraderTask(user2, coin1, 1, 1);
        TraderTask trader3 = new TraderTask(user3, coin2, 1, 1);

        Thread trader1Thread = new Thread(trader1, "Trader-1");
        Thread trader2Thread = new Thread(trader2, "Trader-2");
        Thread trader3Thread = new Thread(trader3, "Trader-3");

        PriceMaker btcPriceMaker = new PriceMaker(coin1, 0.01, 0.01);
        PriceMaker ethPriceMaker = new PriceMaker(coin2, 0.01, 0.01);
        PriceMaker solPriceMaker = new PriceMaker(coin3, 0.01, 0.01);

        Thread btcPriceThread = new Thread(btcPriceMaker, "BTC-PriceMaker");
        Thread ethPriceThread = new Thread(ethPriceMaker, "ETH-PriceMaker");
        Thread solPriceThread = new Thread(solPriceMaker, "SOL-PriceMaker");

        trader1Thread.start();
        trader2Thread.start();
        trader3Thread.start();

        btcPriceThread.start();
        ethPriceThread.start();
        solPriceThread.start();

        Scanner input = new Scanner(System.in);

        while (true) {
            String command = input.nextLine().trim();

            if (command.equalsIgnoreCase("stop")) {
                break;
            }
        }

        trader1.stop();
        trader2.stop();
        trader3.stop();

        btcPriceMaker.stop();
        ethPriceMaker.stop();
        solPriceMaker.stop();

        trader1Thread.interrupt();
        trader2Thread.interrupt();
        trader3Thread.interrupt();

        btcPriceThread.interrupt();
        ethPriceThread.interrupt();
        solPriceThread.interrupt();

        try {
            trader1Thread.join();
            trader2Thread.join();
            trader3Thread.join();

            btcPriceThread.join();
            ethPriceThread.join();
            solPriceThread.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        input.close();

        Coin[] coins = {coin1, coin2, coin3};
        User[] users = {user1, user2, user3};

        Methods.createResultFile(coins, users);

        System.out.println("All threads stopped.");
        System.out.println("Program closed.");
    }
}