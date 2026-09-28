package Thread_Programming_project1.Threads;

import Thread_Programming_project1.Warehouse;

public class StockMonitor implements Runnable {

    private final Warehouse warehouse;
    private volatile boolean running = true;

    public StockMonitor(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void run() {

        while (running) {

            warehouse.checkLowStock();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }
}