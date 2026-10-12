package Thread_Programming_project3.Threads;

import Thread_Programming_project3.system.Market;
import Thread_Programming_project3.Methods;

import java.util.Random;

public class MarketUpdater implements Runnable{
    Market market;
    public MarketUpdater(Market market){
        this.market = market;
    }
    @Override
    public void run() {
        Random random= new Random();
        while (!Thread.currentThread().isInterrupted()) {
            Methods.updateCoinPrices(this.market,random,this.market.getMarketTrend(),this.market.getMarketVolatility());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
