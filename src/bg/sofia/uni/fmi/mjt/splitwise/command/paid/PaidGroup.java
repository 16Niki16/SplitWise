package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.group.GroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

public class PaidGroup implements PaidGroupAPI {
    private static final int GROUP_INDEX = 3;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator exceptions;
    private ReaderWriterCreator tempNotif;
    private ReaderWriterCreator friends;
    private User user;

    public PaidGroup(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications,
                     ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif, ReaderWriterCreator friends,
                     User user) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
        this.friends = friends;
        this.user = user;
    }

    @Override
    public String personPaidToGroup(Command command) {
        try {

            GroupAPI updateGroup = Group.ofSplit(Helpers.findGroupLine(command, groupsDirectory, GROUP_INDEX));
            String payment = updateGroup.payInGroup(command, notifications, tempNotif, friends, user);

            Helpers.addInformation(Helpers.updatedGroup(command.args()[GROUP_INDEX], groupsDirectory, payment),
                groupsDirectory);
            return "Successful payment in a group!";

        } catch (GroupDoesNotExistException | NoMembersToPayException | PersonNotFriendException |
                 FriendNotRegisteredException e) {

            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();

        } catch (IOException e) {

            ExceptionFormater.exceptionAdd(command.line(), "Problem pay in group IO", e.getStackTrace(), exceptions);
            throw new RuntimeException("Server problem pay in group", e);
        }
    }

}
