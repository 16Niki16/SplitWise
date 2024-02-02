package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class OweLessThanYouGivesException extends Exception {
    public OweLessThanYouGivesException(String message) {
        super(message);
    }

    public OweLessThanYouGivesException(String message, Throwable clause) {
        super(message, clause);
    }
}
