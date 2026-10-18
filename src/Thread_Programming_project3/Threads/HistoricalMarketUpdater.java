package Thread_Programming_project3.Threads;

import Thread_Programming_project3.Erros.MarketFileException;
import Thread_Programming_project3.Methods;
import Thread_Programming_project3.system.Coin;
import Thread_Programming_project3.system.Market;

import javax.imageio.plugins.tiff.TIFFDirectory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class HistoricalMarketUpdater implements Runnable {
    String PATH;
    Market market;
    int lenCoins;
    public HistoricalMarketUpdater(String PATH, Market market,int lenCoins) {
        this.PATH = PATH;
        this.market = market;
        this.lenCoins = lenCoins;
    }


    public static int timeToSeconds(String time) {
        if (time == null || !time.matches("\\d{2}:\\d{2}:\\d{2}")) {
            return -1;
        }

        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(3, 5));
        int seconds = Integer.parseInt(time.substring(6, 8));

        if (hours > 23 || minutes > 59 || seconds > 59) {
            return -1;
        }

        return hours * 3600 + minutes * 60 + seconds;
    }


    public static HashMap<String, ArrayList<PriceChangeNode>> FileTxt(String path, int totalLen, int lenCoins,
                                                                      int timeIndex, HashMap<String, Coin> Coins)
            throws MarketFileException {
        if (lenCoins <= 0) {
            throw new MarketFileException(
                    "Invalid market configuration: The market must contain at least one coin."
            );
        }

        if (totalLen != lenCoins + 1 || timeIndex != lenCoins) {
            throw new MarketFileException(
                    "Invalid file configuration: Expected " + lenCoins +
                            " coin columns followed by one 'time' column. " +
                            "Please check totalLen, lenCoins and timeIndex."
            );
        }
        String data;
        int cnt = 0;
        HashMap<String, ArrayList<PriceChangeNode>> result = new HashMap<>();
        HashMap<Integer, String> CoinMap = new HashMap<>();

        try (BufferedReader oos = new BufferedReader(new FileReader(path))) {
            int time,previousTime =-1;
            while ((data = oos.readLine()) != null) {
                String[] parts = data.split(",",-1);

                if (parts.length != totalLen) {
                    throw new MarketFileException(
                            "Line " + (cnt + 1) + ": Invalid number of columns. Expected " + totalLen +
                                    " but found " + parts.length + ". Please check the CSV structure."
                    );
                }

                if (cnt == 0) {

                    if (!parts[timeIndex].equals("time")) {
                        throw new MarketFileException(
                                "Line 1: Invalid time column header '" + parts[timeIndex] +
                                        "'. Expected 'time' at column " + (timeIndex + 1) +
                                        ". Please correct the header of your market data file."
                        );
                    }

                    for (int i = 0; i < lenCoins; i++) {
                        if (!Coins.containsKey(parts[i])) {
                            throw new MarketFileException(
                                    "Line 1: Unknown coin '" + parts[i] + "'. " +
                                            "Please make sure every coin in the header is registered in the market."
                            );
                        } else if (result.containsKey(parts[i])) {
                            throw new MarketFileException(
                                    "Line 1: Duplicate coin '" + parts[i] + "'. " +
                                            "Each coin must appear only once in the header."
                            );
                        } else {
                            result.put(parts[i], new ArrayList<>());
                            CoinMap.put(i, parts[i]);
                        }
                    }
                } else {


                    try {
                        if (timeToSeconds(parts[timeIndex]) == -1) {
                            throw new MarketFileException(
                                    "Line " + (cnt + 1) + ": Invalid time value '" + parts[timeIndex] + "'. " +
                                            "Expected format: HH:MM:SS (00:00:00 to 23:59:59)."
                            );
                        }

                        time = timeToSeconds(parts[timeIndex]);
                        if (previousTime > time) {
                            throw new MarketFileException(
                                    "Line " + (cnt + 1) + ": Invalid timestamp order. Current time (" + time +
                                            " seconds) is earlier than the previous time (" + previousTime +
                                            " seconds). Timestamps must be in ascending order. Please correct the time "
                                            + "values in your market data file."
                            );
                        }
                        if (previousTime == time) {
                            throw new MarketFileException(
                                    "Line " + (cnt + 1) + ": Duplicate timestamp '" + parts[timeIndex] +
                                            "'. Multiple price updates at the same second are not allowed. " +
                                            "Each timestamp must be unique and strictly greater than the previous one."
                            );
                        }

                    } catch (MarketFileException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new MarketFileException(
                                "Line " + (cnt + 1) + ": Could not read the time column. " +
                                        "Please check the timeIndex and the time format.", e
                        );
                    }

                    double price = 0;
                    String str_price;

                    for (int i = 0; i < lenCoins; i++) {
                        str_price = parts[i];

                        if (str_price.equals("NULL")) {
                            continue;
                        }
                        try {
                            price = Double.parseDouble(str_price);
                            if (!Double.isFinite(price) || price <= 0) {
                                throw new MarketFileException(
                                        "Line " + (cnt + 1) + ": Invalid price '" + str_price +
                                                "' for coin '" + CoinMap.get(i) +
                                                "'. Price must be a positive, finite number."
                                );
                            }
                        } catch (MarketFileException e) {
                            throw e;
                        } catch (NumberFormatException e) {
                            throw new MarketFileException(
                                    "Line " + (cnt + 1) + ": Invalid price '" + str_price +
                                            "' for coin '" + CoinMap.get(i) +
                                            "'. Expected a numeric value or NULL.", e
                            );
                        }

//                        ArrayList<PriceChangeNode> pricesArrList = result.get(CoinMap.get(i));
//                        PriceChangeNode newNode = new PriceChangeNode(time, price);
//                        pricesArrList.add(newNode);
//                        result.put(CoinMap.get(i), pricesArrList);
                        result.get(CoinMap.get(i)).add(new PriceChangeNode(time, price));
                    }
                    previousTime = time;
                }
                cnt++;
            }
        } catch (MarketFileException e) {
            throw e;
        } catch (Exception e) {
            throw new MarketFileException(
                    "Failed to load market data from '" + path + "' near line " + (cnt + 1) +
                            ". Please check the file path, permissions and data structure. Details: "
                            + e.getMessage(), e);
        }
        if (cnt < 2) {
            throw new MarketFileException(
                    "Invalid market file: The file is empty or contains only a header. " +
                            "Please provide a valid header and at least one row of market price data."
            );
        }

        for (var entry : result.entrySet()) {
            if (entry.getValue().isEmpty()) {
                throw new MarketFileException(
                        "Invalid market file: Coin '" + entry.getKey() +
                                "' has no valid price data. Please provide at least one " +
                                "positive price for this coin before starting the simulation."
                );
            }
        }


        return result;
    }




    @Override
    public void run() {

    }

}
