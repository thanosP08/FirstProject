# Investment System Simulation

This is a simple Java console project that simulates a basic investment / product trading system.

The program stores data in files, so products and user holdings can remain saved after the program closes.

## Features

- Create products
- Buy products
- Sell products
- Show all available products
- Show owned products
- Change product prices
- Change product names
- Restock products
- Set minimum stock quantity
- Delete products
- Automatic stock monitoring
- Automatic restocking when stock becomes too low
- Set an automatic sell price for a product
- Automatically sell a product when its price reaches the selected limit

## Threads

The project uses background threads for some automatic actions.

- `StockMonitor` checks product quantities and restocks products when needed.
- `PriceLimitMonitor` checks product prices and automatically sells owned products when they reach the selected price limit.

## Data Storage

The program uses `.bin` files to save data such as:

- Products
- Owned products
- Product IDs
- Price limits

Java serialization is used to save and load the data.

## Main Concepts Used

- Java OOP
- Threads
- Runnable
- synchronized
- volatile
- HashMap
- TreeMap
- LinkedHashMap
- File handling
- Serialization