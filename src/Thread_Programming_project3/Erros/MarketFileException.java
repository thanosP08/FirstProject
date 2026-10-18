
package Thread_Programming_project3.Erros;

public class MarketFileException extends Exception {
    public MarketFileException(String message) {
        super(message);
    }
    public MarketFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
