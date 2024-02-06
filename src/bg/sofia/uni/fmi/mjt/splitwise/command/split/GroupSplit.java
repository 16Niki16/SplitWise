package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.group.GroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupSplit implements GroupSplitAPI {
    private static final int GROUP_NAME = 0;
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
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            Helpers.checkGroupExist(command.args()[GROUP_INDEX], groupsDirectory);
            List<String> info = new ArrayList<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    GroupAPI updateGroup = Group.ofSplit(line);
                    info.add(updateGroup.addInformation(command, notifications, tempNotif));
                } else {
                    info.add(line);
                }
            }
            Helpers.addInformation(info, groupsDirectory);
        } catch (GroupDoesNotExistException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Group does not exist", e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Group file IO exception.", e.getStackTrace(), exceptions);
            throw new RuntimeException("Group not found.", e);
        } catch (NumberFormatException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Number format not correct", e.getStackTrace(), exceptions);
            return "Number format exception";
        }
        return "Information successfully added";
    }
}
