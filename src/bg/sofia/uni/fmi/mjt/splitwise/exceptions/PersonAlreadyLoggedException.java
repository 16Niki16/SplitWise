package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PersonAlreadyLoggedException extends RuntimeException {
    public PersonAlreadyLoggedException(String message) {
        super(message);
    }

    public PersonAlreadyLoggedException(String message, Throwable thr) {
        super(message, thr);
    }
}
