package Thread_Programming_project2;

import java.io.Serializable;
import java.util.HashMap;

import static Thread_Programming_project2.Methods.*;

public class User implements Serializable {

    private final String id;
    private String name;
    private double money;
    private final double startingMoney;
    private HashMap<String, Double> coins;

    private static final String ID_PATH =
            "src/Project2/Files/userIDs.bin";

    private final String USERS_DATA_PATH;

    public User(String name, double money) {
        this.id = idSet(ID_PATH);
        this.name = name;
        this.money = money;
        this.startingMoney = money;
        this.coins = new HashMap<>();
        this.USERS_DATA_PATH =
                "src/Project2/Files/UserData/user" + id + ".bin";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }

    public HashMap<String, Double> getCoins() {
        return coins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getStartingMoney() {
        return startingMoney;
    }

    public void setCoins(HashMap<String, Double> coins) {
        this.coins = coins;
    }

    public void buyCoin(String coin_id, int amount) {

        HashMap<String, Integer> userDataMap =
                getFile(this.USERS_DATA_PATH);

        if (userDataMap == null) {
            userDataMap = new HashMap<>();
        }

        userDataMap.put(
                coin_id,
                userDataMap.getOrDefault(coin_id, 0) + amount
        );

        safeFile(this.USERS_DATA_PATH, userDataMap);
    }

    public boolean sellCoin(String coin_id, int amount) {

        HashMap<String, Integer> userDataMap =
                getFile(this.USERS_DATA_PATH);

        if (userDataMap == null) {
            return false;
        }

        if (!userDataMap.containsKey(coin_id)) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (userDataMap.get(coin_id) < amount) {
            return false;
        }

        int newAmount =
                userDataMap.get(coin_id) - amount;

        if (newAmount == 0) {
            userDataMap.remove(coin_id);
        } else {
            userDataMap.put(coin_id, newAmount);
        }

        safeFile(this.USERS_DATA_PATH, userDataMap);

        return true;
    }

    public HashMap<String, Integer> getUserCoins() {

        HashMap<String, Integer> userDataMap =
                getFile(this.USERS_DATA_PATH);

        if (userDataMap == null) {
            return new HashMap<>();
        }

        return userDataMap;
    }
}