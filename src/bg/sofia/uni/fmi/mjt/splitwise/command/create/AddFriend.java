package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class AddFriend implements AddFriendAPI {
    private String directory;

    public AddFriend(String directory) {
        this.directory = directory;
    }

    @Override
    public String addingFriend(String username, String... friend) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            checkInFile(friend[FRIEND_NAME]);
            checkAlreadyFriends(username, friend[FRIEND_NAME]);
            String lineRead;
            List<String> lines = new ArrayList<>();
            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");
                if (splitedUser[USER].trim().equals(username)) {
                    lines.add(addFriends(splitedUser.length != THREE, lineRead, friend[1]));
                } else if (splitedUser[USER].trim().equals(friend[1])) {
                    lines.add(addFriends(splitedUser.length != THREE, lineRead, username));
                } else {
                    lines.add(lineRead);
                }
            }
            try (BufferedWriter wr = new BufferedWriter(new FileWriter(directory, false))) {
                for (String updatedLine : lines) {
                    wr.write(updatedLine);
                    wr.newLine();
                }
            }
        } catch (FriendNotRegisteredException | AlreadyFriendsException ee) {
            return ee.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.format("Friend %s is added.", friend[1]);
    }

    private String addFriends(boolean isFirst, String lineRead, String... friend) {
        StringBuilder addFriend = new StringBuilder(lineRead);
        if ((isFirst)) {
            addFriend.append(" | ").append(friend[0]).append(" 0");
        } else {
            addFriend.append(", ").append(friend[0]).append(" 0");
        }
        return addFriend.toString();
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

    private void checkAlreadyFriends(String username, String friend) throws AlreadyFriendsException {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].trim().equals(username)) {
                    if (splitU.length == THREE) {
                        String[] splitedFriends = splitU[TWO].split(",");
                        for (String fr : splitedFriends) {
                            String[] extrFriend = fr.split(" ");
                            if (extrFriend[FRIEND_NAME].trim().equals(friend)) {
                                throw new AlreadyFriendsException("You are already friends");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
