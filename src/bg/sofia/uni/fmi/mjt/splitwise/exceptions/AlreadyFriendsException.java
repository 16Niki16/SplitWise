package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class AlreadyFriendsException extends Exception {
    public AlreadyFriendsException(String message) {
        super(message);
    }

    public AlreadyFriendsException(String message, Throwable clause) {
        super(message, clause);
    }
}
