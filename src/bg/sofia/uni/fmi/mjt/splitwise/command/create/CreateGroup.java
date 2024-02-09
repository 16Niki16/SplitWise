package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.constants.Constants;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughParticipantsForGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;

public class CreateGroup implements CreateGroupAPI {
    private ReaderWriterCreator friends;
    private ReaderWriterCreator group;
    private ReaderWriterCreator exception;
    private static final int GROUP_NAME = 0;

    public CreateGroup(ReaderWriterCreator friends, ReaderWriterCreator groupsDirectory,
                       ReaderWriterCreator exception) {
        this.friends = friends;
        this.group = groupsDirectory;
        this.exception = exception;
    }

    @Override
    public String createGroup(String username, String... participants) {
        try {
            if (participants.length < FOUR) {
                throw new NotEnoughParticipantsForGroupException("Groups participants are not enough.");
            }
            checkAllExist(participants);
            checkGroupName(participants[Constants.GROUP_NAME]);
            Group newGroup = Group.of(username, participants);
            return "Group: " + appendToFile(newGroup.toString());
        } catch (NotEnoughParticipantsForGroupException | FriendNotRegisteredException | GroupAlreadyExistException e) {
            ExceptionFormater.exceptionAdd(username, e.getLocalizedMessage(), e.getStackTrace(), exception);
            return e.getLocalizedMessage();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(username, "mistake in file creating group.", e.getStackTrace(), exception);
            throw new RuntimeException("Creating group fail IO.", e);
        }
    }

    private void checkGroupName(String name) throws GroupAlreadyExistException, IOException {
        try (BufferedReader r = new BufferedReader(group.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] getName = line.split("\\|");
                if (name.trim().equals(getName[GROUP_NAME].trim())) {
                    throw new GroupAlreadyExistException("Group with this name already exist.");
                }
            }
        }
    }

    private void checkAllExist(String... participants) throws FriendNotRegisteredException, IOException {
        for (int i = FRIEND_LIST; i < participants.length; i++) {
            Helpers.checkInFile(participants[i], friends);
        }
    }

    private String appendToFile(String groupp) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(group.getAppend())) {
            wr.write(groupp);
            wr.newLine();
            wr.flush();
            return groupp;
        }
    }

}
