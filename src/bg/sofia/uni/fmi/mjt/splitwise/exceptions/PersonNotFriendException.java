package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PersonNotFriendException extends RuntimeException {
    public PersonNotFriendException(String message) {
        super(message);
    }

    public PersonNotFriendException(String message, Throwable clause) {
        super(message, clause);
    }
}
