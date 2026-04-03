package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.util.Map;

public interface GroupAPI {
    /**
     * check group contains person
     */
    boolean checkPersonContains(String user);

    /**
     * add owes to group
     */
    String addOwes(User user, Map<String, Double> currencies);

    /**
     * add new balance to group people
     */
    String addInformation(
        CommandLine command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif, double amount);

    /**
     * make payment in a group
     */
    String payInGroup(CommandLine command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif,
                      ReaderWriterCreator friends, User user, double amount)
            throws NoMembersToPayException, PersonNotFriendException, IOException, FriendNotRegisteredException;

    /**
     * get group name
     */
    String getGroupName();

}
