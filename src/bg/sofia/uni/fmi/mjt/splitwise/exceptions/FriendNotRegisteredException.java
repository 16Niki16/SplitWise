package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class FriendNotRegisteredException extends Exception {
    public FriendNotRegisteredException(String message) {
        super(message);
    }

    public FriendNotRegisteredException(String message, Throwable clause) {
        super(message, clause);
    }
}
