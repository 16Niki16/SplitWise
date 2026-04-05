package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PasswordNotCorrectException extends RuntimeException {
    public PasswordNotCorrectException(String message) {
        super(message);
    }

    public PasswordNotCorrectException(String message, Throwable clause) {
        super(message, clause);
    }
}
