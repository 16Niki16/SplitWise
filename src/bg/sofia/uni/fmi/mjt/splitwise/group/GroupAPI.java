package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

public interface GroupAPI {
    /**
     * check group contains person
     * */
    boolean checkPersonContains(String user);

    /**
     * add owes to group
     * */
    String addOwes();

    /**
     * add new balance to group people
     */
    String addInformation(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif);

    /**
     * make payment in a group
     */
    String payInGroup(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif,
                      ReaderWriterCreator friends, User user)
        throws NoMembersToPayException, PersonNotFriendException, IOException, FriendNotRegisteredException;

    /**
     * get group name
     */
    String getGroupName();

}
