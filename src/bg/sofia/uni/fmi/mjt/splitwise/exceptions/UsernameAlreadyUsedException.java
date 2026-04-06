package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }

    public UsernameAlreadyUsedException(String message, Throwable thr) {
        super(message, thr);
    }
}
