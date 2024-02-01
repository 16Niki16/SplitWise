package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class FriendNotRegistered extends Exception {
    public FriendNotRegistered(String message) {
        super(message);
    }

    public FriendNotRegistered(String message, Throwable clause) {
        super(message, clause);
    }
}
