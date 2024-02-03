package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaidGroup implements PaidGroupAPI {
    private static final int GROUP_NAME = 0;
    private static final int GROUP_INDEX = 3;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;

    public PaidGroup(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
    }

    @Override
    public String personPaidToGroup(Command command) {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            checkGroupExist(command.args()[GROUP_INDEX]);
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                String[] getData = line.split("\\|");
                if (getData[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    Group updateGroup = Group.ofSplit(line);
                    lines.add(updateGroup.payInGroup(command, notifications));
                } else {
                    lines.add(line);
                }
            }
            appendNewInformation(lines);
            return "Successful payment in a group!";
        } catch (GroupDoesNotExistException e) {
            return e.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkGroupExist(String name) throws GroupDoesNotExistException, IOException {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(name)) {
                    return;
                }
            }
            throw new GroupDoesNotExistException("Group with that name does not exist");
        }
    }

    private void appendNewInformation(List<String> lines) {
        try (BufferedWriter wr = new BufferedWriter(groupsDirectory.getNotAppend())) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
