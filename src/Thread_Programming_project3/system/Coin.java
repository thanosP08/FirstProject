package Thread_Programming_project3.system;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coin implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String symbol;
    private String name;
    private double currentPrice;
    private double volatility;
    private ArrayList<Double> priceHistory;
    private final String COIN_PATH;

    public Coin(String symbol, String name,
                double currentPrice, double volatility) {

        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.volatility = volatility;
        this.COIN_PATH = "src/Thread_Programming_project3/Files/Coins/coin"+symbol+".bin";
        this.priceHistory = new ArrayList<>(List.of(currentPrice));

    }

    public synchronized double getCurrentPrice() {
        return currentPrice;
    }

    public synchronized void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.priceHistory.add(currentPrice);
    }

    public synchronized ArrayList<Double> getPriceHistory() {
        return new ArrayList<>(priceHistory);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }


    public String getCOIN_PATH() {
        return COIN_PATH;
    }


}