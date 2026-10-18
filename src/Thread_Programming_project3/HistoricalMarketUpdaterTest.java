
package Thread_Programming_project3;

import Thread_Programming_project3.Erros.MarketFileException;
import Thread_Programming_project3.Threads.HistoricalMarketUpdater;
import Thread_Programming_project3.Threads.PriceChangeNode;
import Thread_Programming_project3.system.Coin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoricalMarketUpdaterTest {

    private static int passed = 0;
    private static int failed = 0;

    private static HashMap<String, Coin> createCoins() {
        HashMap<String, Coin> coins = new HashMap<>();

        // Only containsKey() is used by FileTxt().
        coins.put("BTC", null);
        coins.put("ETH", null);

        return coins;
    }

    private static Path createFile(String content) throws Exception {
        Path file = Files.createTempFile("market_test_", ".txt");
        Files.writeString(file, content);
        file.toFile().deleteOnExit();
        return file;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    @FunctionalInterface
    interface TestCase {
        void execute() throws Exception;
    }

    private static void test(String name, TestCase testCase) {
        System.out.println("\n========== " + name + " ==========");

        try {
            testCase.execute();
            passed++;
            System.out.println("[PASS]");

        } catch (Throwable e) {
            failed++;
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    private static void expectError(
            String content, String expectedMessage) throws Exception {

        Path file = createFile(content);

        try {
            HistoricalMarketUpdater.FileTxt(
                    file.toString(), 3, 2, 2, createCoins()
            );

            throw new AssertionError(
                    "Expected MarketFileException, but none was thrown."
            );

        } catch (MarketFileException e) {
            System.out.println("Exception: " + e.getMessage());

            check(
                    e.getMessage().contains(expectedMessage),
                    "Unexpected exception message."
            );
        }
    }

    private static void checkNode(
            ArrayList<PriceChangeNode> nodes,
            int index, int expectedTime, double expectedPrice) {

        PriceChangeNode node = nodes.get(index);

        check(node.getTime() == expectedTime,
                "Incorrect time at index " + index);

        check(Double.compare(node.getPrice(), expectedPrice) == 0,
                "Incorrect price at index " + index);
    }

    public static void main(String[] args) {

        // TEST 1: Valid file and correct stored values
        test("01_VALID_DATA", () -> {

            Path file = createFile(
                    "BTC,ETH,time\n" +
                            "65000,3200,00:00:00\n" +
                            "NULL,3210,00:00:05\n" +
                            "65200,NULL,00:00:10\n" +
                            "65300,3300,00:00:20\n"
            );

            HashMap<String, ArrayList<PriceChangeNode>> result =
                    HistoricalMarketUpdater.FileTxt(
                            file.toString(), 3, 2, 2, createCoins()
                    );

            ArrayList<PriceChangeNode> btc = result.get("BTC");
            ArrayList<PriceChangeNode> eth = result.get("ETH");

            check(result.size() == 2, "Wrong number of coins.");

            check(btc.size() == 3, "BTC should have 3 changes.");
            check(eth.size() == 3, "ETH should have 3 changes.");

            checkNode(btc, 0, 0, 65000);
            checkNode(btc, 1, 10, 65200);
            checkNode(btc, 2, 20, 65300);

            checkNode(eth, 0, 0, 3200);
            checkNode(eth, 1, 5, 3210);
            checkNode(eth, 2, 20, 3300);

            System.out.println("BTC: 3 correct nodes");
            System.out.println("ETH: 3 correct nodes");
        });

        // TEST 2: Invalid time column header
        test("02_INVALID_HEADER", () ->
                expectError(
                        "BTC,ETH,wrongName\n65000,3200,00:00:00",
                        "Invalid time column header"
                )
        );

        // TEST 3: Duplicate coin
        test("03_DUPLICATE_COIN", () ->
                expectError(
                        "BTC,BTC,time\n65000,3200,00:00:00",
                        "Duplicate coin"
                )
        );

        // TEST 4: Unknown coin
        test("04_UNKNOWN_COIN", () ->
                expectError(
                        "BTC,SOL,time\n65000,3200,00:00:00",
                        "Unknown coin"
                )
        );

        // TEST 5: Invalid timestamp
        test("05_INVALID_TIME", () ->
                expectError(
                        "BTC,ETH,time\n65000,3200,00:70:00",
                        "Invalid time value"
                )
        );

        // TEST 6: Duplicate timestamp
        test("06_DUPLICATE_TIME", () ->
                expectError(
                        "BTC,ETH,time\n" +
                                "65000,3200,00:00:05\n" +
                                "65100,3210,00:00:05",
                        "Duplicate timestamp"
                )
        );

        // TEST 7: Incorrect chronological order
        test("07_UNSORTED_TIME", () ->
                expectError(
                        "BTC,ETH,time\n" +
                                "65000,3200,00:00:10\n" +
                                "65100,3210,00:00:05",
                        "Invalid timestamp order"
                )
        );

        // TEST 8: Extra comma
        test("08_EXTRA_COMMA", () ->
                expectError(
                        "BTC,ETH,time,\n65000,3200,00:00:00,",
                        "Invalid number of columns"
                )
        );

        // TEST 9: Missing column
        test("09_MISSING_COLUMN", () ->
                expectError(
                        "BTC,ETH,time\n65000,00:00:00",
                        "Invalid number of columns"
                )
        );

        // TEST 10: Negative price
        test("10_NEGATIVE_PRICE", () ->
                expectError(
                        "BTC,ETH,time\n-65000,3200,00:00:00",
                        "positive, finite number"
                )
        );

        // TEST 11: Zero price
        test("11_ZERO_PRICE", () ->
                expectError(
                        "BTC,ETH,time\n0,3200,00:00:00",
                        "positive, finite number"
                )
        );

        // TEST 12: NaN price
        test("12_NAN_PRICE", () ->
                expectError(
                        "BTC,ETH,time\nNaN,3200,00:00:00",
                        "positive, finite number"
                )
        );

        // TEST 13: Infinity price
        test("13_INFINITY_PRICE", () ->
                expectError(
                        "BTC,ETH,time\nInfinity,3200,00:00:00",
                        "positive, finite number"
                )
        );

        // TEST 14: Invalid numeric format
        test("14_INVALID_PRICE", () ->
                expectError(
                        "BTC,ETH,time\nabc,3200,00:00:00",
                        "Expected a numeric value or NULL"
                )
        );

        // TEST 15: Empty file
        test("15_EMPTY_FILE", () ->
                expectError(
                        "",
                        "empty or contains only a header"
                )
        );

        // TEST 16: Header only
        test("16_HEADER_ONLY", () ->
                expectError(
                        "BTC,ETH,time\n",
                        "empty or contains only a header"
                )
        );

        // TEST 17: One coin has no valid prices
        test("17_COIN_WITHOUT_PRICES", () ->
                expectError(
                        "BTC,ETH,time\n" +
                                "NULL,3200,00:00:00\n" +
                                "NULL,3250,00:00:05",
                        "has no valid price data"
                )
        );

        // TEST 18: All values are NULL
        test("18_ALL_NULL", () ->
                expectError(
                        "BTC,ETH,time\n" +
                                "NULL,NULL,00:00:00\n" +
                                "NULL,NULL,00:00:05",
                        "has no valid price data"
                )
        );

        // TEST 19: Invalid configuration
        test("19_INVALID_CONFIGURATION", () -> {

            Path file = createFile(
                    "BTC,ETH,time\n65000,3200,00:00:00"
            );

            try {
                HistoricalMarketUpdater.FileTxt(
                        file.toString(), 3, 2, 1, createCoins()
                );
                throw new AssertionError("Expected configuration error.");

            } catch (MarketFileException e) {
                check(e.getMessage().contains("Invalid file configuration"),
                        "Wrong configuration error.");
            }
        });

        // TEST 20: File does not exist
        test("20_MISSING_FILE", () -> {

            Path directory = Files.createTempDirectory("market_missing_");
            Path missing = directory.resolve("does_not_exist.txt");

            try {
                HistoricalMarketUpdater.FileTxt(
                        missing.toString(), 3, 2, 2, createCoins()
                );
                throw new AssertionError("Expected file error.");

            } catch (MarketFileException e) {
                check(e.getMessage().contains("Failed to load market data"),
                        "Wrong missing-file error.");
            }
        });

        System.out.println("\n=================================");
        System.out.println("          TEST RESULTS");
        System.out.println("=================================");
        System.out.println("PASSED: " + passed);
        System.out.println("FAILED: " + failed);
        System.out.println("TOTAL : " + (passed + failed));
    }
}
