package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupAlreadyExistException extends Exception {
    public GroupAlreadyExistException(String message) {
        super(message);
    }

    public GroupAlreadyExistException(String message, Throwable clause) {
        super(message, clause);
    }
}
