package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class PersonNotFriend extends Exception {
    public PersonNotFriend(String message) {
        super(message);
    }

    public PersonNotFriend(String message, Throwable clause) {
        super(message, clause);
    }
}
