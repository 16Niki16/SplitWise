package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupSplit implements GroupSplitAPI {
    private static final int GROUP_NAME = 0;
    private static final int GROUP_INDEX = 2;
    private String groupsDirectory;

    public GroupSplit(String groupsDirectory) {
        this.groupsDirectory = groupsDirectory;
    }

    @Override
    public String groupsOwe(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            checkGroupExist(command.args()[GROUP_INDEX]);
            List<String> info = new ArrayList<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    Group updateGroup = Group.ofSplit(line);
                    info.add(updateGroup.addInformation(command));
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

    private void checkGroupExist(String name) throws GroupDoesNotExistException {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(name)) {
                    return;
                }
            }
            throw new GroupDoesNotExistException("Group with that name does not exist");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendNewInformation(List<String> lines) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(groupsDirectory, false))) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
