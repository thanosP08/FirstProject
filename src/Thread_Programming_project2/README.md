# Thread_Programming_project2

A Java console project focused on multithreading, shared resources, synchronization, and thread-safe programming.

The program simulates multiple users trading cryptocurrencies while separate threads update coin prices according to recent trading activity.

## Project Goal

The main purpose of this project is to practice Java thread programming and understand how multiple threads interact with shared objects.

The trading system is intentionally simplified. The main focus of the project is concurrency rather than creating a realistic financial market.

## What the Program Does

- Creates multiple users with starting balances.
- Creates multiple cryptocurrency objects.
- Runs each trader as a separate thread.
- Traders randomly choose between:
    - BUY
    - SELL
    - WAIT
- Multiple traders can access the same coin.
- Separate `PriceMaker` threads update coin prices.
- Coin prices are influenced by recent buy and sell volume.
- User holdings are stored using binary files.
- Coin information is stored using Java serialization.
- The simulation continues until the user types `stop`.
- All worker threads are stopped before the program exits.
- A final text report is generated with simulation statistics.

## Thread Concepts Used

### Runnable

Both `TraderTask` and `PriceMaker` implement the `Runnable` interface.

```java
public class TraderTask implements Runnable
```

```java
public class PriceMaker implements Runnable
```

This allows each task to be executed by a separate `Thread`.

### Thread

Trader and price-maker tasks are executed using separate thread objects.

Example:

```java
Thread traderThread = new Thread(traderTask);
traderThread.start();
```

### synchronized

Shared `Coin` objects are used as synchronization locks.

```java
synchronized (coin) {
    // critical section
}
```

This prevents multiple threads from modifying the same coin at the same time.

For example, a trader cannot complete a transaction on a coin while its `PriceMaker` thread is updating the same coin.

### volatile

Thread control variables use `volatile`.

```java
private volatile boolean run;
```

This makes changes performed by the main thread visible to worker threads.

It is used to safely request that a thread stops running.

### AtomicInteger

Global BUY and SELL counters use `AtomicInteger`.

```java
private static final AtomicInteger buys = new AtomicInteger(0);
private static final AtomicInteger sells = new AtomicInteger(0);
```

The counters are updated using:

```java
buys.incrementAndGet();
sells.incrementAndGet();
```

This prevents race conditions when different trader threads update the counters at the same time.

### ThreadLocalRandom

Trader actions are selected using:

```java
ThreadLocalRandom.current()
```

Each trader randomly chooses between BUY, SELL, and WAIT.

### interrupt()

During shutdown, threads are interrupted:

```java
thread.interrupt();
```

This allows a sleeping thread to wake up immediately instead of waiting for `Thread.sleep()` to finish.

### join()

The main thread waits for every worker thread to terminate:

```java
thread.join();
```

The simulation report is generated only after all trader and price-maker threads have finished.

## Trading System

Each `TraderTask` is connected to:

- One `User`
- One shared `Coin`
- A BUY amount
- A SELL amount

During every loop iteration, the trader randomly selects an action.

### BUY

The trader checks:

- Whether the user has enough money.
- Whether the coin has enough available quantity.

The complete transaction is executed inside:

```java
synchronized (coin)
```

### SELL

The trader checks whether the user owns enough of the selected coin.

If the sale is valid:

- The user's coin holdings decrease.
- The user's money increases.
- The available coin quantity increases.
- Sell volume is recorded.

### WAIT

The trader temporarily sleeps before selecting another action.

## PriceMaker

Each cryptocurrency has its own `PriceMaker` thread.

The price maker periodically reads the recent BUY and SELL volume of its coin.

The price impact is calculated using:

```java
double impact = coin.getBuyVolume() * buyImpact
        - coin.getSellVolume() * sellImpact;
```

The new price is calculated using:

```java
coin.setPrice(coin.getPrice() * (1 + impact));
```

After the price update:

```java
coin.setBuyVolume(0);
coin.setSellVolume(0);
```

The volumes are reset so the next update only considers new trading activity.

The `PriceMaker` also synchronizes on the same `Coin` object used by the traders.

## File Storage

The project uses Java serialization and file I/O.

Main classes used include:

- `FileOutputStream`
- `ObjectOutputStream`
- `FileInputStream`
- `ObjectInputStream`
- `FileWriter`
- `PrintWriter`

Binary files are used to store:

- Generated user IDs
- Generated coin IDs
- Coin information
- User coin holdings

Each coin has its own serialized file.

Each user has a separate file containing their coin holdings.

## Automatic Folder Creation

Runtime data is stored inside:

```text
src/Thread_Programming_project2/Files/
```

The `Files` directory does not need to exist before the program starts.

The `safeFile()` method automatically creates the required parent directories when necessary.

Because binary files are generated while the program is running, they are excluded from Git using `.gitignore`.

## Simulation Result

When the user types:

```text
stop
```

the program:

1. Requests all trader threads to stop.
2. Requests all price-maker threads to stop.
3. Interrupts sleeping threads.
4. Waits for every thread using `join()`.
5. Generates the final simulation report.

The report is created at:

```text
src/Thread_Programming_project2/Files/result/result.txt
```

The report contains:

- Total BUY operations
- Total SELL operations
- Total trades
- Final price of each coin
- Final available quantity of each coin
- Starting money of each user
- Final money of each user
- Money difference
- Final user coin holdings

Example:

```text
================================
      TRADING SIMULATION
================================

GENERAL STATISTICS
------------------
Total Buys: 12
Total Sells: 8
Total Trades: 20

COINS
-----
BTC - Bitcoin
Final Price: 2575.25
Final Quantity: 47.00

USERS
-----
James
Starting Money: 12000.00
Final Money: 9449.75
Money Difference: -2550.25
Coins:
BTC -> 1
```

## Project Structure

```text
src/
└── Thread_Programming_project2/
    ├── Coin.java
    ├── Main.java
    ├── Methods.java
    ├── User.java
    ├── README.md
    └── Threads/
        ├── PriceMaker.java
        └── TraderTask.java
```

The following directory is created automatically while the program is running:

```text
Files/
├── Coins/
├── UserData/
└── result/
```

## Main Technologies and Concepts

- Java
- Object-Oriented Programming
- Multithreading
- `Runnable`
- `Thread`
- Shared resources
- Critical sections
- `synchronized`
- `volatile`
- `AtomicInteger`
- `ThreadLocalRandom`
- `interrupt()`
- `join()`
- Java Serialization
- File I/O

## Notes

The `coins` field inside the `User` class is currently unused.

It is intentionally kept for a possible future version where user portfolios may also be stored directly in memory.

The simulation uses simplified cryptocurrency pricing logic and is not intended to represent a real cryptocurrency exchange or financial market.

## Possible Future Improvements

Future versions could include:

- `wait()` and `notifyAll()`
- `ExecutorService`
- Thread pools
- Deadlock detection
- Deadlock prevention
- More detailed statistics
- Improved persistence
- More advanced market simulation

## Purpose

This project was created primarily to practice and understand Java concurrency.

The main objective is to experiment with multiple threads accessing shared mutable state and to apply synchronization and thread-management techniques correctly.