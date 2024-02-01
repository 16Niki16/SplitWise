package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughParticipantsForGroupException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class CreateGroup implements CreateGroupAPI {
    private String directory;
    private String groupsDirectory;

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
            StringBuilder buildingGroup = new StringBuilder(participants[GROUP_NAME]).append(" | ")
                .append(username).append(" 0");
            for (int i = FRIEND_LIST; i < participants.length; i++) {
                checkInFile(participants[i]);
                buildingGroup.append(", ").append(participants[i]).append(" 0");
            }
            return appendToFile(buildingGroup);
        } catch (NotEnoughParticipantsForGroupException | FriendNotRegisteredException e) {
            return e.getLocalizedMessage();
        }
    }

    private String appendToFile(StringBuilder group) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(groupsDirectory, true))) {
            wr.write(String.valueOf(group));
            wr.newLine();
            return group.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkInFile(String username) throws FriendNotRegisteredException {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].trim().equals(username)) {
                    return;
                }
            }
            throw new FriendNotRegisteredException(String.format("%s is not registered yet", username));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
