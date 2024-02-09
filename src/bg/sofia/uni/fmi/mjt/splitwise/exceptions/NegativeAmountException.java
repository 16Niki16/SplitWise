package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NegativeAmountException extends Exception {
    public NegativeAmountException(String message) {
        super(message);
    }

    public NegativeAmountException(String message, Throwable clause) {
        super(message, clause);
    }
}
