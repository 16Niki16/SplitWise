package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.constants.Constants;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CreateGroupFileException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;

public class CreateGroup implements CreateGroupAPI {
    private final ReaderWriterCreator friends;
    private final ReaderWriterCreator group;
    private static final int GROUP_NAME = 0;

    public CreateGroup(ReaderWriterCreator friends, ReaderWriterCreator groupsDirectory) {
        this.friends = friends;
        this.group = groupsDirectory;
    }

    @Override
    public String createGroup(CommandLine command) {
        try {
            checkAllExist(command);
            checkGroupName(command.args()[Constants.GROUP_NAME]);
            Group newGroup = Group.of(command);
            Helpers.appendToFile(newGroup.toString(), group);
            return "Group is successfully created!";

        } catch (IOException e) {
            throw new CreateGroupFileException("Creating group IO problem.", e);
        }
    }

    private void checkGroupName(String name) throws GroupAlreadyExistException, IOException {
        try (BufferedReader r = new BufferedReader(group.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] getName = line.split("\\|");

                if (name.equals(getName[GROUP_NAME])) {
                    throw new GroupAlreadyExistException("Group with this name already exist.");
                }
            }
        }
    }

    private void checkAllExist(CommandLine command) {
        Set<String> users =
            new HashSet<>(Arrays.asList(Arrays.copyOfRange(command.args(), FRIEND_LIST, command.args().length)));

        if (users.contains(command.line())) {
            throw new AddYourselfException("You are trying to add yourself second time in a group!");
        }

        Helpers.checkInFileGroup(users, friends);
    }

}
