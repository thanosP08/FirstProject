package Thread_Programming_project3.Threads;

public class PriceChangeNode {
    private int time;
    private double price;
    public PriceChangeNode(int time, double price) {
        this.time = time;
        this.price = price;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
