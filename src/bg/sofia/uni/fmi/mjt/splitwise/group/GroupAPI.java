package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NegativeAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

public interface GroupAPI {
    /**
     * add new balance to group people
     */
    String addInformation(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif)
        throws NegativeAmountException;

    /**
     * make payment in a group
     */
    String payInGroup(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif)
        throws NegativeAmountException, NoMembersToPayException;

    /**
     * get group name
     */
    String getGroupName();

}
