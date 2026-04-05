package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PersonNotRegisteredException extends RuntimeException {
    public PersonNotRegisteredException(String message) {
        super(message);
    }

    public PersonNotRegisteredException(String message, Throwable thr) {
        super(message, thr);
    }
}
