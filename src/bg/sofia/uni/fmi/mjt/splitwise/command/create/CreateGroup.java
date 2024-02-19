package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.constants.Constants;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
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
    private final ReaderWriterCreator exception;
    private static final int GROUP_NAME = 0;

    public CreateGroup(ReaderWriterCreator friends, ReaderWriterCreator groupsDirectory,
                       ReaderWriterCreator exception) {
        this.friends = friends;
        this.group = groupsDirectory;
        this.exception = exception;
    }

    @Override
    public String createGroup(Command command) {
        try {
            checkAllExist(command);
            checkGroupName(command.args()[Constants.GROUP_NAME]);
            Group newGroup = Group.of(command);
            Helpers.appendToFile(newGroup.toString(), group);
            return "Group is successfully created!";

        } catch (FriendNotRegisteredException | GroupAlreadyExistException | AddYourselfException e) {
            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(), exception);
            return e.getLocalizedMessage();

        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "mistake in file creating group.", e.getStackTrace(),
                exception);
            throw new RuntimeException("Creating group fail IO.", e);
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

    private void checkAllExist(Command command) throws FriendNotRegisteredException, IOException, AddYourselfException {
        Set<String> users =
            new HashSet<>(Arrays.asList(Arrays.copyOfRange(command.args(), FRIEND_LIST, command.args().length)));
        if (users.contains(command.line())) {
            throw new AddYourselfException("You are trying to add yourself second time in a group!");
        }
        Helpers.checkInFileGroup(users, friends);
    }

}
