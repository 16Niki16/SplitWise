package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class SessionNotActiveException extends RuntimeException {
    public SessionNotActiveException(String message) {
        super(message);
    }

    public SessionNotActiveException(String message, Throwable thr) {
        super(message, thr);
    }
}
