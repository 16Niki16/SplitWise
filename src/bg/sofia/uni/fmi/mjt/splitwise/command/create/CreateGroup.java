package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.constants.Constants;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughParticipantsForGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class CreateGroup implements CreateGroupAPI {
    private String directory;
    private String groupsDirectory;
    private static final int GROUP_NAME = 0;

    public CreateGroup(String directory, String groupsDirectory) {
        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
    }

    @Override
    public String createGroup(String username, String... participants) {
        try {
            if (participants.length < FOUR) {
                throw new NotEnoughParticipantsForGroupException("groups participants are not enough");
            }
            checkAllExist(participants);
            checkGroupName(participants[Constants.GROUP_NAME]);
            Group newGroup = Group.of(username, participants);
            return appendToFile(newGroup.toString());
        } catch (NotEnoughParticipantsForGroupException | FriendNotRegisteredException | GroupAlreadyExistException e) {
            return e.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkGroupName(String name) throws GroupAlreadyExistException, IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] getName = line.split("\\|");
                if (name.trim().equals(getName[GROUP_NAME].trim())) {
                    throw new GroupAlreadyExistException("Group with this name already exist");
                }
            }
        }
    }

    private void checkAllExist(String... participants) throws FriendNotRegisteredException, IOException {
        for (int i = FRIEND_LIST; i < participants.length; i++) {
            checkInFile(participants[i]);
        }
    }

    private String appendToFile(String group) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(groupsDirectory, true))) {
            wr.write(group);
            wr.newLine();
            return group;
        }
    }

    private void checkInFile(String username) throws FriendNotRegisteredException, IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].trim().equals(username)) {
                    return;
                }
            }
            throw new FriendNotRegisteredException(String.format("%s is not registered yet", username));
        }
    }
}
