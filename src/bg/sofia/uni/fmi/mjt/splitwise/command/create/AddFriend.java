package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class AddFriend implements AddFriendAPI {
    private String directory;
    private User user;

    public AddFriend(String directory, User user) {
        this.directory = directory;
        this.user = user;
    }

    @Override
    public String addingFriend(String username, String... friend) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            checkInFile(friend[FRIEND_NAME]);
            this.user.checkAlreadyFriends(friend[FRIEND_NAME]);
            String lineRead;
            List<String> lines = new ArrayList<>();

            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");

                if (splitedUser[USER].trim().equals(username)) {
                    lines.add(user.addFriend(friend[FRIEND_NAME]));
                } else if (splitedUser[USER].trim().equals(friend[FRIEND_NAME])) {
                    User friendU = User.of(lineRead);
                    lines.add(friendU.addFriend(username));
                } else {
                    lines.add(lineRead);
                }
            }

            addNewInformation(lines);
        } catch (FriendNotRegisteredException | AlreadyFriendsException ee) {
            return ee.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.format("Friend %s is added.", friend[1]);
    }

    private void addNewInformation(List<String> information) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(directory, false))) {
            for (String updatedLine : information) {
                wr.write(updatedLine);
                wr.newLine();
            }
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
            throw new FriendNotRegisteredException("This person is not registered yet");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
