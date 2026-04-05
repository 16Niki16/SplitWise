package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class CommandNotKnownException extends RuntimeException {
    public CommandNotKnownException(String message) {
        super(message);
    }

    public CommandNotKnownException(String message, Throwable thr) {
        super(message, thr);
    }
}
