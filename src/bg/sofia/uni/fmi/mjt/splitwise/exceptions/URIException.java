package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class URIException extends RuntimeException {
    public URIException(String message) {
        super(message);
    }

    public URIException(String message, Throwable thr) {
        super(message, thr);
    }
}
