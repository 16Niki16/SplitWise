package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class CreateGroupFileException extends RuntimeException {
    public CreateGroupFileException(String message) {
        super(message);
    }

    public CreateGroupFileException(String message, Throwable thr) {
        super(message, thr);
    }
}
