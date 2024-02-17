package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class CurrencyMapException extends Exception {
    public CurrencyMapException(String message) {
        super(message);
    }

    public CurrencyMapException(String message, Throwable clause) {
        super(message, clause);
    }
}
