package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotEnoughArgumentsException extends Exception {
    public NotEnoughArgumentsException(String message) {
        super(message);
    }

    public NotEnoughArgumentsException(String message, Throwable clause) {
        super(message, clause);
    }
}
