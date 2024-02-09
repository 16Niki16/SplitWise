package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotNumberException extends Exception {
    public NotNumberException(String message) {
        super(message);
    }

    public NotNumberException(String message, Throwable clause) {
        super(message, clause);
    }
}
