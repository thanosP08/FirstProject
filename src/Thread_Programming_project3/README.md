# Crypto Market Simulator (Work in Progress)

> **Status: In development — not a finished application.**

This is a Java learning project exploring multithreading, simulated cryptocurrency prices, portfolio tracking, and basic financial calculations. The codebase is still being developed and may contain incomplete features or bugs.

## What can you run right now?

For now, the only intended entry point is **`Main2.java`** (`Thread_Programming_project3.Main2`). It runs a small test simulation with predefined coins and a demo portfolio:

- Creates a simulated market and sample coins.
- Starts the `MarketUpdater` thread, which updates prices using random values.
- Starts the `PortfolioUpdater` thread for the current experimental portfolio logic.
- Prints market and portfolio information during the test run.
- Stops the threads at the end of the test.

**To try it:** Open the project in a Java IDE, make sure the source folders/packages are configured correctly, and run the `main` method in `Main2.java`.

## What is not ready yet?

- The main user-facing menu and full application workflow.
- Loading and replaying historical coin prices from CSV/TXT files.
- Complete testing and error handling.
- A fully verified investment strategy and thread-safety guarantees.

The simulated prices are **randomly generated**, not live market prices. This project is for programming practice and experimentation only; it is **not a real trading platform**.

## Development status

The project will be updated as the remaining features are implemented and tested. This repository is being shared now to document development progress.
