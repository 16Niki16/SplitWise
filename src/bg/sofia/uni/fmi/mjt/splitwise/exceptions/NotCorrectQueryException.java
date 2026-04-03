package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotCorrectQueryException extends RuntimeException {
    public NotCorrectQueryException(String message) {
        super(message);
    }

    public NotCorrectQueryException(String message, Throwable clause) {
        super(message, clause);
    }
}
