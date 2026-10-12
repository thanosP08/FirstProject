package Thread_Programming_project3.Threads;

import Thread_Programming_project3.Methods;

import javax.imageio.plugins.tiff.TIFFDirectory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;

public class HistoricalMarketUpdater implements Runnable {
    String PATH;
    int coinsLen;
    public HistoricalMarketUpdater(String PATH,int coinsLen) {
        this.PATH = PATH;
        this.coinsLen = coinsLen;
    }



    @Override
    public void run() {

    }

}
