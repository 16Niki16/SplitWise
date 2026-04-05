package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class IOProblemException extends RuntimeException {
    public IOProblemException(String message) {
        super(message);
    }

    public IOProblemException(String message, Throwable thr) {
        super(message, thr);
    }
}
