package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class CurrencyConversionException extends RuntimeException {
    public CurrencyConversionException(String message) {
        super(message);
    }

    public CurrencyConversionException(String message, Throwable thr) {
        super(message, thr);
    }
}
