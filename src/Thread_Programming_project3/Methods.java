package Thread_Programming_project3;

import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public abstract class Methods {
    public static <T extends Serializable> void safeFile(String filePath, T data) {
        File file = new File(filePath);
        File parentFolder = file.getParentFile();

        if (parentFolder != null && !parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(data);

        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T getFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (T) ois.readObject();

        } catch (Exception e) {
            return null;
        }
    }


    public synchronized static String idSet(String idFilePath) {
        HashSet<String> idSet = getFile(idFilePath);

        if (idSet == null) {
            idSet = new HashSet<>();
        }

        Random rand = new Random();

        while (true) {
            String idNumber = String.valueOf(rand.nextInt(9000) + 1000);

            if (!idSet.contains(idNumber)) {
                idSet.add(idNumber);
                safeFile(idFilePath, idSet);
                return idNumber;
            }
        }
    }

    public static void updateCoinPrices(
            Market market,
            Random random,
            double marketTrend,
            double marketVolatility) {

        if (marketVolatility < 0) {
            throw new IllegalArgumentException("Invalid volatility");
        }

        // Κοινή τυχαία επίδραση σε ολόκληρη την αγορά
        double marketShock = random.nextGaussian();

        for (Coin coin : market.getCoins().values()) {
            synchronized (coin) {// Ανεξάρτητη τυχαία επίδραση σε κάθε coin
                double coinShock = random.nextGaussian();

                double coinVolatility = coin.getVolatility();

                double variance = Math.pow(marketVolatility, 2)
                        + Math.pow(coinVolatility, 2);

                // Υπολογισμός μεταβολής
                double change = marketTrend
                        - 0.5 * variance
                        + marketVolatility * marketShock
                        + coinVolatility * coinShock;

                // Νέα τιμή
                double newPrice = coin.getCurrentPrice() * Math.exp(change);

                coin.setCurrentPrice(newPrice);}

        }
    }
    public static boolean isDigit(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private static long parseTimeToSeconds(String time) {

        if (time == null) {
            return -1;
        }

        String[] times = time.split(":", -1);

        if (times.length != 3) {
            return -1;
        }

        try {
            long timeH = Long.parseLong(times[0]);
            long timeM = Long.parseLong(times[1]);
            long timeS = Long.parseLong(times[2]);

            if (timeH < 0 || timeM < 0 || timeM >= 60
                    || timeS < 0 || timeS >= 60) {
                return -1;
            }

            return Math.addExact(
                    Math.multiplyExact(timeH, 3600L),
                    60L * timeM + timeS
            );

        } catch (NumberFormatException | ArithmeticException e) {
            return -1;
        }
    }




    public static Integer historyCheck(String path,int coinsLen,Market market) throws IOException {

        try (BufferedReader reader = new BufferedReader(
                new FileReader(path))) {

            String line;
            int cnt = 0;
            long timeNow = -1;

            HashSet<String> symbols = new HashSet<>();

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(",", -1);

                // 1. Έλεγχος πλήθους στηλών
                if (columns.length - 1 != coinsLen) {
                    return 1;
                }

                // 2. Έλεγχος πρώτης γραμμής (Header)
                if (cnt == 0) {

                    if (!columns[columns.length - 1].trim()
                            .equalsIgnoreCase("time")) {
                        return 7;
                    }

                    for (int i = 0; i < coinsLen; i++) {

                        String symbol = columns[i].trim();

                        if (symbol.isEmpty()
                                || !symbols.add(symbol.toUpperCase())) {
                            return 7;
                        }
                    }

                } else {

                    // 3. Έλεγχος χρόνου
                    String timeInput = columns[columns.length - 1];
                    long time = parseTimeToSeconds(timeInput);
                    if (time == -1) {
                        return 4;
                    }
                    if (time <= timeNow) {
                        return 5;
                    }
                    timeNow = time;
                    // 4. Αφαιρούμε τη στήλη του χρόνου
                    columns = Arrays.copyOfRange(
                            columns, 0, columns.length - 1
                    );
                    // 5. Έλεγχος όλων των τιμών
                    for (int i = 0; i < columns.length; i++) {
                        double price;
                        try {
                            price = Double.parseDouble(columns[i].trim());
                        } catch (NumberFormatException e) {
                            return 2;
                        }
                        if (!Double.isFinite(price) || price <= 0) {
                            return 3;
                        }
                    }
                }
                cnt++;
            }
            // 6. Κενό αρχείο ή μόνο Header
            if (cnt < 2) {
                return 6;
            }
            if (!symbols.equals(market.getCoins().keySet())) {
                return 8;
            }
        }
        return 0;
    }
}
