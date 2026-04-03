package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class AddFriendException extends RuntimeException {
    public AddFriendException(String message) {
        super(message);
    }

    public AddFriendException(String message, Throwable thr) {
        super(message, thr);
    }
}
