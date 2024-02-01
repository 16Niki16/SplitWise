package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotEnoughParticipantsForGroup extends Exception {
    public NotEnoughParticipantsForGroup(String message) {
        super(message);
    }

    public NotEnoughParticipantsForGroup(String message, Throwable clause) {
        super(message, clause);
    }
}
