package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class AddYourselfException extends RuntimeException {
    public AddYourselfException(String message) {
        super(message);
    }

    public AddYourselfException(String message, Throwable clause) {
        super(message, clause);
    }
}
