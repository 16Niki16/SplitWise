package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupDoesNotExistException extends Exception {
    public GroupDoesNotExistException(String message) {
        super(message);
    }

    public GroupDoesNotExistException(String message, Throwable clause) {
        super(message, clause);
    }
}
