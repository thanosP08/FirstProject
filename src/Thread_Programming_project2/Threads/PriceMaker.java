package Thread_Programming_project2.Threads;

import Thread_Programming_project2.Coin;

public class PriceMaker implements Runnable {

    private final Coin coin;
    private final double buyImpact;
    private final double sellImpact;
    private volatile boolean running;

    public PriceMaker(Coin coin, double buyImpact, double sellImpact) {
        this.coin = coin;
        this.buyImpact = buyImpact;
        this.sellImpact = sellImpact;
        this.running = true;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {

            synchronized (coin) {
                double impact = coin.getBuyVolume() * buyImpact
                        - coin.getSellVolume() * sellImpact;

                if (impact != 0) {
                    coin.setPrice(coin.getPrice() * (1 + impact));
                }

                coin.setBuyVolume(0);
                coin.setSellVolume(0);
                coin.coinUpdate();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}