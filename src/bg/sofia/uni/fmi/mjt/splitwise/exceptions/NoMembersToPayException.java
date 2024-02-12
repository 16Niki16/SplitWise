package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NoMembersToPayException extends Exception {
    public NoMembersToPayException(String message) {
        super(message);
    }

    public NoMembersToPayException(String message, Throwable clause) {
        super(message, clause);
    }
}
