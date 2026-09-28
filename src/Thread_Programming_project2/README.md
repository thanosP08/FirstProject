# Thread_Programming_project2

A Java console project focused on multithreading, shared resources, synchronization, and thread-safe programming.

This project simulates multiple users trading cryptocurrencies while separate threads update coin prices based on market activity.

## Project Goal

The main purpose of this project is to practice Java thread programming and understand how multiple threads interact with shared objects.

The trading logic is intentionally simple. The main focus is concurrency.

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
- Coin prices are affected by recent buy and sell volume.
- User holdings are stored in binary files.
- Coin data is stored using Java serialization.
- The simulation stops when the user types `stop`.
- All threads are stopped safely before the program exits.
- A final `.txt` file is created containing simulation statistics.

## Thread Concepts Used

### Runnable

Both `TraderTask` and `PriceMaker` implement the `Runnable` interface.

```java
public class TraderTask implements Runnable