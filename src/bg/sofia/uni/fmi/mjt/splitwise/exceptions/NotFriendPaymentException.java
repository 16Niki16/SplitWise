package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotFriendPaymentException extends Exception {
    public NotFriendPaymentException(String message) {
        super(message);
    }

    public NotFriendPaymentException(String message, Throwable clause) {
        super(message, clause);
    }
}
