package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class DataException extends RuntimeException {
    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable thr) {
        super(message, thr);
    }
}
