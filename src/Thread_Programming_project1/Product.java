package Thread_Programming_project1;

import java.io.Serializable;

public class Product implements Serializable {

    private String name;
    private int quantity;
    private int minimumQuantity;
    private double price;

    private final String id;

    private static final String ID_PATH =
            "src/Thread_Programming_project/Files/product_ids.bin";

    public Product(String name, int quantity, int minimumQuantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.minimumQuantity = minimumQuantity;
        this.price = price;
        this.id = Methods.idSet(ID_PATH);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}