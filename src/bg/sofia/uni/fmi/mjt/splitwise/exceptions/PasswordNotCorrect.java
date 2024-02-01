package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PasswordNotCorrect extends Exception {
    public PasswordNotCorrect(String message) {
        super(message);
    }

    public PasswordNotCorrect(String message, Throwable clause) {
        super(message, clause);
    }
}
