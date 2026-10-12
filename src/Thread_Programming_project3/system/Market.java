package Thread_Programming_project3.system;

import java.io.Serializable;
import java.util.HashMap;

public class Market implements Serializable {

    private static final long serialVersionUID = 1L;
    double marketTrend;
    double marketVolatility;
    private final HashMap<String, Coin> coins;

    public Market(double marketTrend, double marketVolatility) {
        this.marketTrend = marketTrend;
        this.marketVolatility = marketVolatility;
        this.coins = new HashMap<>();
    }

    public HashMap<String, Coin> getCoins() {
        return new HashMap<>(coins);
    }
    public Double getMarketTrend() {
        return this.marketTrend;
    }
    public Double getMarketVolatility() {
        return this.marketVolatility;
    }

    public void addCoin(Coin coin) {
        this.coins.put(coin.getSymbol(), coin);
    }

    public Coin findCoin(String symbol) {
        return coins.get(symbol);
    }

    public boolean removeCoin(String symbol) {
        return coins.remove(symbol) != null;
    }

    public void printCoins() {
        for (String key : this.coins.keySet()) {
            Coin thisCoin = this.coins.get(key);

            System.out.println(
                    "Symbol: " + thisCoin.getSymbol() +
                            " | Name: " + thisCoin.getName() +
                            " | Price: " + thisCoin.getCurrentPrice() +
                            " | Volatility: " + (thisCoin.getVolatility() * 100) + "%"
            );
        }
    }
}