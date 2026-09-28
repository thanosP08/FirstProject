//not ready yet

package Thread_Programming_project1.Threads;

import Thread_Programming_project1.Warehouse;

import java.util.HashMap;

import static Thread_Programming_project1.Methods.getFile;

public class PercentageMonitor implements Runnable {

    private final Warehouse warehouse;

    private static final String INITIAL_PRICES_PATH =
            "src/Thread_Programming_project/Files/initial_prices.bin";

    private static final String PERCENTAGE_LIMITS_PATH =
            "src/Thread_Programming_project/Files/percentage_limits.bin";

    private HashMap<String, Double> initialPrices;
    private HashMap<String, Double> percentageLimits;

    private volatile boolean running = true;

    public PercentageMonitor(Warehouse warehouse) {

        this.warehouse = warehouse;

        this.initialPrices = getFile(INITIAL_PRICES_PATH);
        this.percentageLimits = getFile(PERCENTAGE_LIMITS_PATH);

        if (initialPrices == null) {
            initialPrices = new HashMap<>();
        }

        if (percentageLimits == null) {
            percentageLimits = new HashMap<>();
        }
    }

    @Override
    public void run() {

    }

    public void stop() {
        running = false;
    }
}