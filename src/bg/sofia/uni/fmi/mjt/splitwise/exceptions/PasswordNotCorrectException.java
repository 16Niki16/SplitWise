package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PasswordNotCorrectException extends Exception {
    public PasswordNotCorrectException(String message) {
        super(message);
    }

    public PasswordNotCorrectException(String message, Throwable clause) {
        super(message, clause);
    }
}
