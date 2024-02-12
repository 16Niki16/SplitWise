package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class MoreMoneyException extends Exception {
    public MoreMoneyException(String message) {
        super(message);
    }

    public MoreMoneyException(String message, Throwable clause) {
        super(message, clause);
    }
}
