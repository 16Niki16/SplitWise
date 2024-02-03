package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupSplit implements GroupSplitAPI {
    private static final int GROUP_NAME = 0;
    private static final int GROUP_INDEX = 2;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;

    public GroupSplit(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
    }

    @Override
    public String groupsOwe(Command command) {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            checkGroupExist(command.args()[GROUP_INDEX]);
            List<String> info = new ArrayList<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    Group updateGroup = Group.ofSplit(line);
                    info.add(updateGroup.addInformation(command, notifications));
                } else {
                    info.add(line);
                }
            }
            appendNewInformation(info);
        } catch (GroupDoesNotExistException e) {
            return e.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Information successfully added";
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

    private void appendNewInformation(List<String> lines) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(groupsDirectory.getNotAppend())) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        }
    }
}
