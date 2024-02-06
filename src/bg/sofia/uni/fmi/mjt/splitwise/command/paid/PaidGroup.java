package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

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

public class PaidGroup implements PaidGroupAPI {
    private static final int GROUP_NAME = 0;
    private static final int GROUP_INDEX = 3;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator exceptions;
    private ReaderWriterCreator tempNotif;

    public PaidGroup(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications,
                     ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
    }

    @Override
    public String personPaidToGroup(Command command) {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            Helpers.checkGroupExist(command.args()[GROUP_INDEX], groupsDirectory);
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                String[] getData = line.split("\\|");
                if (getData[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    GroupAPI updateGroup = Group.ofSplit(line);
                    lines.add(updateGroup.payInGroup(command, notifications, tempNotif));
                } else {
                    lines.add(line);
                }
            }
            Helpers.addInformation(lines, groupsDirectory);
            return "Successful payment in a group!";
        } catch (GroupDoesNotExistException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Group does not exist", e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Problem pay in group IO", e.getStackTrace(), exceptions);
            throw new RuntimeException("Server problem pay in group", e);
        } catch (NumberFormatException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Number format is not correct", e.getStackTrace(),
                exceptions);
            return "Number format is not correct";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Not enough arguments";
        }
    }

}
