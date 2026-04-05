package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupAlreadyExistException extends RuntimeException {
    public GroupAlreadyExistException(String message) {
        super(message);
    }

    public GroupAlreadyExistException(String message, Throwable clause) {
        super(message, clause);
    }
}
