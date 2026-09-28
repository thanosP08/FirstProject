package Thread_Programming_project2.Threads;

import Thread_Programming_project2.Coin;
import Thread_Programming_project2.User;

import java.util.concurrent.ThreadLocalRandom;

enum ActionType {
    BUY, SELL, WAIT
}

public class TraderTask implements Runnable {

    private final User user;
    private final Coin coin;
    private final int buyAmount;
    private final int sellAmount;
    private volatile boolean run;

    public TraderTask(User user, Coin coin, int buyAmount, int sellAmount) {
        this.user = user;
        this.coin = coin;
        this.buyAmount = buyAmount;
        this.sellAmount = sellAmount;
        this.run = true;
    }

    public void stop() {
        run = false;
    }

    @Override
    public void run() {
        ActionType[] actions = ActionType.values();

        while (run) {
            int randomIndex = ThreadLocalRandom.current().nextInt(actions.length);
            ActionType action = actions[randomIndex];

            System.out.println(user.getName() + " chose " + action);

            if (action == ActionType.BUY) {
                synchronized (coin) {
                    double cost = coin.getPrice() * buyAmount;

                    if (user.getMoney() >= cost && coin.getQuantity() >= buyAmount) {
                        coin.buyTheCoin(buyAmount);
                        user.setMoney(user.getMoney() - cost);
                        user.buyCoin(coin.getId(), buyAmount);
                    }
                }

            } else if (action == ActionType.SELL) {
                synchronized (coin) {
                    if (user.sellCoin(coin.getId(), sellAmount)) {
                        double moneyFromSell = coin.getPrice() * sellAmount;

                        user.setMoney(user.getMoney() + moneyFromSell);
                        coin.sellTheCoin(sellAmount);
                    }
                }

            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}