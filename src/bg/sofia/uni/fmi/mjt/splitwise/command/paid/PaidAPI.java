package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;

public interface PaidAPI {
    /**
     * made payment to a person
     * */
    String personPay(Command command) throws PersonNotFriendException, FriendNotRegisteredException;
}
