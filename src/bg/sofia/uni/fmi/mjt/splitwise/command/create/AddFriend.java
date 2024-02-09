package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class AddFriend implements AddFriendAPI {
    private ReaderWriterCreator creator;
    private User user;

    private ReaderWriterCreator exceptionDirectory;

    public AddFriend(ReaderWriterCreator creator, User user, ReaderWriterCreator exceptionDirectory) {
        this.creator = creator;
        this.user = user;
        this.exceptionDirectory = exceptionDirectory;
    }

    @Override
    public String addingFriend(String username, String... friend) {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            Helpers.checkInFile(friend[FRIEND_NAME], creator);
            this.user.checkAlreadyFriends(friend[FRIEND_NAME]);
            String lineRead;
            List<String> lines = new ArrayList<>();

            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");

                if (splitedUser[USER].trim().equals(username)) {
                    lines.add(user.addFriend(friend[FRIEND_NAME]));
                } else if (splitedUser[USER].trim().equals(friend[FRIEND_NAME])) {
                    UserAPI friendU = User.of(lineRead);
                    lines.add(friendU.addFriend(username));
                } else {
                    lines.add(lineRead);
                }
            }
            Helpers.addInformation(lines, creator);
        } catch (FriendNotRegisteredException | AlreadyFriendsException ee) {
            ExceptionFormater.exceptionAdd(username, ee.getLocalizedMessage(), ee.getStackTrace(), exceptionDirectory);
            return ee.getLocalizedMessage();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(username, "Add friend directory mistake", e.getStackTrace(),
                exceptionDirectory);
            throw new RuntimeException("Could not add the friend successfully", e);
        }
        return String.format("Friend %s is added.", friend[1]);
    }
}
