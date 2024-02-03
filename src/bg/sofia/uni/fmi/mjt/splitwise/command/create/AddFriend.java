package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class AddFriend implements AddFriendAPI {
    private ReaderWriterCreator creator;
    private User user;

    public AddFriend(ReaderWriterCreator creator, User user) {
        this.creator = creator;
        this.user = user;
    }

    @Override
    public String addingFriend(String username, String... friend) {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
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
        try (BufferedWriter wr = new BufferedWriter(creator.getNotAppend())) {
            for (String updatedLine : information) {
                wr.write(updatedLine);
                wr.newLine();
            }
        }
    }

    private void checkInFile(String username) throws FriendNotRegisteredException, IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].trim().equals(username)) {
                    return;
                }
            }
            throw new FriendNotRegisteredException("This person is not registered yet.");
        }
    }
}
