package Thread_Programming_project3.Threads;

import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;
import Thread_Programming_project3.Methods;
import Thread_Programming_project3.system.Portfolio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

enum InvestmentDecision { BUY, SELL, HOLD }

public class PortfolioUpdater implements Runnable {

    private static final double RISK_AVERSION = 4.0;
    private static final int MIN_HISTORY_PRICES = 3;
    private static final int MAX_CANDIDATES = 3;
    private static final int GRID_UNITS = 10;       // 1 μονάδα = 10%
    private static final int MAX_COIN_UNITS = 4;   // έως 40% ανά coin
    private static final int MAX_INVESTED_UNITS = 8; // τουλάχιστον 20% cash
    private static final double MAX_POSITIVE_CORRELATION = 0.95;
    private static final double MIN_TRADE_VALUE = 5.0;
    private static final double MIN_UTILITY_GAIN = 0.0001;

    private final Portfolio portfolio;
    private final Market market;

    public PortfolioUpdater(Portfolio portfolio, Market market) {
        this.portfolio = portfolio;
        this.market = market;
    }

    // === Οι 9 προηγούμενες συναρτήσεις ===

    private double calculateTotalValue() {
        double V = portfolio.getAvailableBalance();
        HashMap<String, Double> assets = portfolio.getAssets();
        for (String symbol : assets.keySet()) {
            Coin coin = market.findCoin(symbol);
            double Qi = assets.get(symbol);
            double Pi = coin.getCurrentPrice();
            V += Qi * Pi;
        }
        return V;
    }

    private ArrayList<Double> calculateReturns(Coin coin) {
        ArrayList<Double> prices = coin.getPriceHistory();
        ArrayList<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double Pi = prices.get(i);
            double previousPi = prices.get(i - 1);
            if (previousPi <= 0) {
                throw new IllegalStateException("Invalid coin price");
            }
            returns.add((Pi - previousPi) / previousPi);
        }
        return returns;
    }

    private double calculateAverageReturn(Coin coin) {
        ArrayList<Double> returns = calculateReturns(coin);
        if (returns.isEmpty()) {
            throw new IllegalStateException("Not enough price history");
        }
        double R = 0;
        for (double r : returns) R += r;
        return R / returns.size();
    }

    private double calculateVariance(Coin coin) {
        ArrayList<Double> returns = calculateReturns(coin);
        if (returns.size() < 2) {
            throw new IllegalStateException("Not enough price history");
        }
        double R = 0;
        double MR = 0;
        for (double r : returns) MR += r;
        MR /= returns.size();
        for (double r : returns) R += Math.pow(r - MR, 2);
        return R / (returns.size() - 1);
    }

    private double calculateStandardDeviation(Coin coin) {
        return Math.sqrt(calculateVariance(coin));
    }

    private double calculateCovariance(Coin A, Coin B) {
        ArrayList<Double> returnsA = calculateReturns(A);
        ArrayList<Double> returnsB = calculateReturns(B);
        if (returnsA.size() < 2 || returnsB.size() < 2) {
            throw new IllegalStateException("Not enough price history to calculate covariance");
        }
        if (returnsA.size() != returnsB.size()) {
            throw new IllegalStateException("Different history lengths: "
                    + A.getSymbol() + ", " + B.getSymbol());
        }
        int n = returnsA.size();
        double MRA = 0, MRB = 0, cov = 0;
        for (int i = 0; i < n; i++) {
            MRA += returnsA.get(i);
            MRB += returnsB.get(i);
        }
        MRA /= n;
        MRB /= n;
        for (int i = 0; i < n; i++) {
            cov += (returnsA.get(i) - MRA) * (returnsB.get(i) - MRB);
        }
        return cov / (n - 1);
    }

    private double calculateCorrelation(Coin A, Coin B) {
        double stdA = calculateStandardDeviation(A);
        double stdB = calculateStandardDeviation(B);
        if (stdA == 0 || stdB == 0) {
            throw new IllegalStateException("Zero standard deviation");
        }
        return calculateCovariance(A, B) / (stdA * stdB);
    }

    private double calculatePortfolioRisk() {
        double V = calculateTotalValue();
        if (V <= 0) return 0;
        HashMap<String, Double> assets = portfolio.getAssets();
        double variance = 0;
        for (String symbolA : assets.keySet()) {
            for (String symbolB : assets.keySet()) {
                Coin A = market.findCoin(symbolA);
                Coin B = market.findCoin(symbolB);
                double cow = calculateCovariance(A, B);
                double wA = assets.get(symbolA) * A.getCurrentPrice() / V;
                double wB = assets.get(symbolB) * B.getCurrentPrice() / V;
                variance += wA * wB * cow;
            }
        }
        return Math.sqrt(Math.max(0, variance));
    }

    private double calculatePortfolioExpectedReturn() {
        double V = calculateTotalValue();
        if (V <= 0) return 0;
        HashMap<String, Double> assets = portfolio.getAssets();
        double expectedReturn = 0;
        for (String symbol : assets.keySet()) {
            double Qi = assets.get(symbol);
            if (Qi == 0) continue;
            Coin coin = market.findCoin(symbol);
            double Pi = coin.getCurrentPrice();
            double wi = (Qi * Pi) / V;
            expectedReturn += wi * calculateAverageReturn(coin);
        }
        return expectedReturn;
    }

    // === Νέες συναρτήσεις για υποψήφια ποσοστά, χωρίς αγορές ===

    private double calculatePortfolioExpectedReturn(Map<String, Double> weights) {
        double result = 0;
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            Coin coin = market.findCoin(entry.getKey());
            result += entry.getValue() * calculateAverageReturn(coin);
        }
        return result;
    }

    private double calculatePortfolioRisk(Map<String, Double> weights) {
        double variance = 0;
        for (Map.Entry<String, Double> a : weights.entrySet()) {
            for (Map.Entry<String, Double> b : weights.entrySet()) {
                Coin A = market.findCoin(a.getKey());
                Coin B = market.findCoin(b.getKey());
                variance += a.getValue() * b.getValue() * calculateCovariance(A, B);
            }
        }
        return Math.sqrt(Math.max(0, variance));
    }

    private double calculateUtility(double expectedReturn, double risk) {
        return expectedReturn - 0.5 * RISK_AVERSION * risk * risk;
    }

    // Επιπλέον κανόνας διαφοροποίησης: απορρίπτουμε δύο μεγάλες,
    // σχεδόν απόλυτα θετικά συσχετισμένες θέσεις.
    private boolean acceptableCorrelation(Map<String, Double> weights) {
        List<String> symbols = new ArrayList<>(weights.keySet());
        for (int i = 0; i < symbols.size(); i++) {
            for (int j = i + 1; j < symbols.size(); j++) {
                Coin A = market.findCoin(symbols.get(i));
                Coin B = market.findCoin(symbols.get(j));
                if (calculateStandardDeviation(A) == 0
                        || calculateStandardDeviation(B) == 0) {
                    return false; // η συσχέτιση δεν ορίζεται
                }
                if (calculateCorrelation(A, B) > MAX_POSITIVE_CORRELATION) {
                    return false;
                }
            }
        }
        return true;
    }

    // Διαλέγουμε έως 3 coins. Διατηρούμε προτεραιότητα σε όσα ήδη κατέχουμε.
    private List<Coin> chooseCandidates() {
        HashMap<String, Coin> available = market.getCoins();
        HashMap<String, Double> owned = portfolio.getAssets();

        int referenceHistorySize = 0;
        for (Coin coin : available.values()) {
            referenceHistorySize = Math.max(referenceHistorySize,
                    coin.getPriceHistory().size());
        }
        if (referenceHistorySize < MIN_HISTORY_PRICES) return List.of();

        ArrayList<Coin> candidates = new ArrayList<>();
        for (String symbol : owned.keySet()) {
            if (owned.get(symbol) <= 0) continue;
            Coin coin = available.get(symbol);
            if (coin == null || coin.getPriceHistory().size() != referenceHistorySize) {
                return List.of(); // δεν εκτελούμε συναλλαγές με ατελή δεδομένα
            }
            candidates.add(coin);
        }
        if (candidates.size() > MAX_CANDIDATES) {
            return List.of(); // απαιτείται μεγαλύτερο όριο/άλλος optimizer
        }

        ArrayList<Coin> other = new ArrayList<>();
        for (Coin coin : available.values()) {
            if (coin.getPriceHistory().size() == referenceHistorySize
                    && coin.getCurrentPrice() > 0
                    && !owned.containsKey(coin.getSymbol())) {
                other.add(coin);
            }
        }
        // Προκαταρκτική κατάταξη με το ίδιο mean-variance κριτήριο.
        other.sort(Comparator
                .comparingDouble((Coin c) ->
                        calculateAverageReturn(c) -
                                0.5 * RISK_AVERSION * calculateVariance(c))
                .reversed()
                .thenComparing(Coin::getSymbol));

        for (Coin coin : other) {
            if (candidates.size() == MAX_CANDIDATES) break;
            candidates.add(coin);
        }
        return candidates;
    }

    private static class BestAllocation {
        double utility = Double.NEGATIVE_INFINITY;
        HashMap<String, Double> weights = new HashMap<>();
    }

    // Grid Search: 0, 10, 20, 30, 40% ανά coin, με >=20% cash.
    private void searchAllocations(List<Coin> candidates,
                                   int index,
                                   int usedUnits,
                                   HashMap<String, Double> current,
                                   BestAllocation best) {
        if (index == candidates.size()) {
            if (!acceptableCorrelation(current)) return;
            double r = calculatePortfolioExpectedReturn(current);
            double risk = calculatePortfolioRisk(current);
            double utility = calculateUtility(r, risk);
            if (Double.isFinite(utility) && utility > best.utility) {
                best.utility = utility;
                best.weights = new HashMap<>(current);
            }
            return;
        }

        String symbol = candidates.get(index).getSymbol();
        for (int units = 0; units <= MAX_COIN_UNITS; units++) {
            if (usedUnits + units > MAX_INVESTED_UNITS) break;
            if (units == 0) current.remove(symbol);
            else current.put(symbol, units / (double) GRID_UNITS);
            searchAllocations(candidates, index + 1,
                    usedUnits + units, current, best);
        }
        current.remove(symbol);
    }

    // Ελέγχει αν η ΤΩΡΙΝΗ κατανομή σέβεται τα ίδια όρια.
    private boolean currentAllocationIsAllowed() {
        double V = calculateTotalValue();
        if (V <= 0 || !Double.isFinite(V)) return false;

        double totalInvestedWeight = 0;
        HashMap<String, Double> weights = new HashMap<>();
        for (Map.Entry<String, Double> entry : portfolio.getAssets().entrySet()) {
            if (entry.getValue() <= 0) continue;
            Coin coin = market.findCoin(entry.getKey());
            double weight = entry.getValue() * coin.getCurrentPrice() / V;
            if (weight > MAX_COIN_UNITS / (double) GRID_UNITS + 1e-9) {
                return false;
            }
            totalInvestedWeight += weight;
            weights.put(entry.getKey(), weight);
        }
        return totalInvestedWeight <= MAX_INVESTED_UNITS / (double) GRID_UNITS + 1e-9
                && acceptableCorrelation(weights);
    }

    private InvestmentDecision determineDecision(double quantity, double price) {
        double value = Math.abs(quantity * price);
        if (!Double.isFinite(value) || value < MIN_TRADE_VALUE) return InvestmentDecision.HOLD;
        return quantity > 0 ? InvestmentDecision.BUY : InvestmentDecision.SELL;
    }

    // Εκτελούμε όλες τις πωλήσεις πριν από τις αγορές.
    private boolean rebalance(Map<String, Double> targetWeights) {
        double V = calculateTotalValue();
        if (V <= 0 || !Double.isFinite(V)) return false;

        HashMap<String, Double> owned = portfolio.getAssets();
        Set<String> symbols = new HashSet<>(owned.keySet());
        symbols.addAll(targetWeights.keySet());

        HashMap<String, Double> prices = new HashMap<>();
        HashMap<String, Double> deltas = new HashMap<>();
        for (String symbol : symbols) {
            Coin coin = market.findCoin(symbol);
            if (coin == null) throw new IllegalStateException("Unknown coin: " + symbol);
            double price = coin.getCurrentPrice();
            if (!Double.isFinite(price) || price <= 0) {
                throw new IllegalStateException("Invalid price: " + symbol);
            }
            double Qi = owned.getOrDefault(symbol, 0.0);
            double wi = targetWeights.getOrDefault(symbol, 0.0);
            prices.put(symbol, price);
            deltas.put(symbol, (wi * V - Qi * price) / price);
        }

        boolean changed = false;
        for (String symbol : symbols) {
            double quantity = deltas.get(symbol);
            double price = prices.get(symbol);
            if (determineDecision(quantity, price) == InvestmentDecision.SELL) {
                double ownedQty = owned.getOrDefault(symbol, 0.0);
                double sellQty = Math.min(-quantity, ownedQty);
                if (portfolio.sell(symbol, sellQty, price)) changed = true;
            }
        }
        for (String symbol : symbols) {
            double quantity = deltas.get(symbol);
            double price = prices.get(symbol);
            if (determineDecision(quantity, price) == InvestmentDecision.BUY) {
                double affordable = portfolio.getAvailableBalance() / price;
                double buyQty = Math.min(quantity, affordable);
                if (buyQty > 0 && buyQty * price >= MIN_TRADE_VALUE
                        && portfolio.buy(symbol, buyQty, price)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            boolean changed = false;
            try {
                // Το MarketUpdater πρέπει να χρησιμοποιεί το ίδιο market lock!
                synchronized (market) {
                    synchronized (portfolio) {
                        List<Coin> candidates = chooseCandidates();
                        if (!candidates.isEmpty()) {
                            double currentReturn = calculatePortfolioExpectedReturn();
                            double currentRisk = calculatePortfolioRisk();
                            double currentUtility = calculateUtility(currentReturn, currentRisk);

                            BestAllocation best = new BestAllocation();
                            searchAllocations(candidates, 0, 0,
                                    new HashMap<>(), best);

                            if (best.utility > currentUtility + MIN_UTILITY_GAIN
                                    || !currentAllocationIsAllowed()) {
                                changed = rebalance(best.weights);
                            }
                        }
                    }
                }

                if (changed) {
                    synchronized (portfolio) {
                        Methods.safeFile(portfolio.getPORTFOLIO_PATH(), portfolio);
                    }
                }

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (RuntimeException e) {
                System.err.println("PortfolioUpdater error: " + e.getMessage());
                break;
            }
        }
    }
}
