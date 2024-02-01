package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupDoesNotExist extends Exception {
    public GroupDoesNotExist(String message) {
        super(message);
    }

    public GroupDoesNotExist(String message, Throwable clause) {
        super(message, clause);
    }
}
