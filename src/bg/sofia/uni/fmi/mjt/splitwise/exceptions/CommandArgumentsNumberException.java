package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class CommandArgumentsNumberException extends RuntimeException {
    public CommandArgumentsNumberException(String message) {
        super(message);
    }

    public CommandArgumentsNumberException(String message, Throwable thr) {
        super(message, thr);
    }
}
