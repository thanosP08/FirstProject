package Thread_Programming_project2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static Thread_Programming_project2.Methods.*;

public class Coin implements Serializable {

    private String symbol;
    private String name;
    private double price;
    private double quantity;
    private int buyVolume;
    private int sellVolume;
    private final String id;

    private static final String ID_PATH =
            "src/Thread_Programming_project2/Files/coinIDs.bin";

    private static final String COINS_PATH =
            "src/Thread_Programming_project2/Files/Coins.bin";

    private final String COIN_PATH;

    private static final AtomicInteger buys = new AtomicInteger(0);
    private static final AtomicInteger sells = new AtomicInteger(0);

    public Coin(String symbol, String name, double price, int quantity) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.buyVolume = 0;
        this.sellVolume = 0;
        this.id = idSet(ID_PATH);

        this.COIN_PATH = "src/Thread_Programming_project2/Files/Coins/Coin_" + id + ".bin";
        safeFile(this.COIN_PATH, this);

        // Currently unused. Kept for a future version with an in-memory portfolio system:
        // (HashMap<String, String> coins).
        HashMap<String, String> coins = getFile(COINS_PATH);

        if (coins == null) {
            coins = new HashMap<>();
        }

        coins.put(this.id, this.COIN_PATH);
        safeFile(COINS_PATH, coins);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getBuyVolume() {
        return buyVolume;
    }

    public int getSellVolume() {
        return sellVolume;
    }

    public void setBuyVolume(int buyVolume) {
        this.buyVolume = buyVolume;
    }

    public void setSellVolume(int sellVolume) {
        this.sellVolume = sellVolume;
    }

    public void buyTheCoin(int amount) {
        buys.incrementAndGet();

        buyVolume += amount;
        this.quantity -= amount;

        safeFile(this.COIN_PATH, this);
    }

    public void sellTheCoin(int amount) {
        sells.incrementAndGet();

        sellVolume += amount;
        this.quantity += amount;

        safeFile(this.COIN_PATH, this);
    }

    public void coinUpdate() {
        safeFile(this.COIN_PATH, this);
    }

    public static int getBuys() {
        return buys.get();
    }

    public static int getSells() {
        return sells.get();
    }
}