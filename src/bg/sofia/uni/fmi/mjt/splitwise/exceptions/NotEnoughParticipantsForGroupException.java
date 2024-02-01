package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotEnoughParticipantsForGroupException extends Exception {
    public NotEnoughParticipantsForGroupException(String message) {
        super(message);
    }

    public NotEnoughParticipantsForGroupException(String message, Throwable clause) {
        super(message, clause);
    }
}
