package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.group.GroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

public class GroupSplit implements GroupSplitAPI {
    private static final int GROUP_INDEX = 2;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator exceptions;
    private ReaderWriterCreator tempNotif;

    public GroupSplit(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications,
                      ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
    }

    @Override
    public String groupsOwe(Command command) {

        try {

            GroupAPI updateGroup = Group.ofSplit(Helpers.findGroupLine(command, groupsDirectory, GROUP_INDEX));
            String payment = updateGroup.addInformation(command, notifications, tempNotif);

            Helpers.addInformation(Helpers.updatedGroup(command.args()[GROUP_INDEX], groupsDirectory, payment),
                groupsDirectory);

        } catch (GroupDoesNotExistException e) {
            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Group file IO exception.", e.getStackTrace(), exceptions);
            throw new RuntimeException("Group not found.", e);
        }
        return "Information successfully added";
    }
}
