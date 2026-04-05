package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException(String message) {
        super(message);
    }

    public UnknownCommandException(String message, Throwable clause) {
        super(message, clause);
    }
}
