package Thread_Programming_project3.system;

import Thread_Programming_project3.Methods;

import java.io.Serializable;
import java.util.HashMap;

public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String accountId;
    private String name;
    private double availableBalance;

    // Asset symbol -> Quantity
    private HashMap<String, Double> assets;

    private final String PORTFOLIO_PATH;

    private static final String ID_SET_PATH =
            "src/Thread_Programming_project3/Files/portfolioIDs.bin";

    public Portfolio(String name, String accountId,double availableBalance) {
        this.id = Methods.idSet(ID_SET_PATH);
        this.accountId = accountId;
        this.name = name;
        this.availableBalance = availableBalance;
        this.assets = new HashMap<>();

        this.PORTFOLIO_PATH =
                "src/Thread_Programming_project3/Files/Portfolios/Portfolio_"
                        + id + ".bin";
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Double> getAssets() {
        return new HashMap<>(assets);
    }

    public void setAssets(HashMap<String, Double> assets) {
        this.assets = new HashMap<>(assets);
    }


    public String getPORTFOLIO_PATH() {
        return PORTFOLIO_PATH;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public synchronized boolean buy(String symbol, double quantity, double price) {
        if (symbol == null || symbol.isBlank()
                || !Double.isFinite(quantity) || quantity <= 0
                || !Double.isFinite(price) || price <= 0) {
            return false;
        }
        double cost = quantity * price;

        if (!Double.isFinite(cost) || cost > availableBalance) {
            return false;
        }
        double owned = assets.getOrDefault(symbol, 0.0);
        double newQuantity = owned + quantity;

        if (!Double.isFinite(newQuantity)) {
            return false;
        }
        assets.put(symbol, newQuantity);
        availableBalance -= cost;
        return true;
    }

    public synchronized boolean sell(String symbol, double quantity, double price) {
        if (symbol == null || symbol.isBlank()
                || !Double.isFinite(quantity) || quantity <= 0
                || !Double.isFinite(price) || price <= 0) {
            return false;
        }
        double owned = assets.getOrDefault(symbol, 0.0);
        if (quantity > owned) {
            return false;
        }
        double revenue = quantity * price;
        if (!Double.isFinite(revenue)
                || !Double.isFinite(availableBalance + revenue)) {
            return false;
        }
        double remaining = owned - quantity;
        if (remaining == 0) {
            assets.remove(symbol);
        } else {
            assets.put(symbol, remaining);
        }
        availableBalance += revenue;
        return true;
    }
}