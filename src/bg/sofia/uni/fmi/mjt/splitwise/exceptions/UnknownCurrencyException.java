package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UnknownCurrencyException extends Exception {
    public UnknownCurrencyException(String message) {
        super(message);
    }

    public UnknownCurrencyException(String message, Throwable clause) {
        super(message, clause);
    }
}
