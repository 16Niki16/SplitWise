package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CreateGroupFileException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroupCommand implements Command {
    private final ReaderWriterCreator friends;
    private final ReaderWriterCreator group;
    private final User creator;
    private static final int GROUP_NAME = 0;

    public CreateGroupCommand(User creator, ReaderWriterCreator friends, ReaderWriterCreator groupsDirectory) {
        this.creator = creator;
        this.friends = friends;
        this.group = groupsDirectory;
    }

    public String execute(String... args) {
        try {
            String groupName = args[0];
            Set<String> participants = getParticipants(args);
            checkAllExist(participants);
            checkGroupName(groupName);
            Group newGroup = Group.of(groupName, creator.getUsername(), participants);
            Helpers.appendToFile(newGroup.toString(), group);
            return "Group is successfully created!";

        } catch (IOException e) {
            throw new CreateGroupFileException("Creating group IO problem.", e);
        }
    }

    private Set<String> getParticipants(String... args) {
        return Arrays.stream(args)
            .skip(1)
            .collect(Collectors.toSet());
    }

    private void checkGroupName(String name) throws IOException {
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

    private void checkAllExist(Set<String> users) {
        Helpers.checkInFileGroup(users, friends);
    }
}
