package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.ClientContainer;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionHandler;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

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
    private ClientContainer container;

    public AddFriend(ReaderWriterCreator creator, User user, ReaderWriterCreator exceptionDirectory,
                     ClientContainer container) {
        this.creator = creator;
        this.user = user;
        this.exceptionDirectory = exceptionDirectory;
        this.container = container;
    }

    @Override
    public String addingFriend(Command command) {
        try {

            ExceptionHandler.checkAddYourself(command.line(), command.args()[FRIEND_NAME]);
            this.user.checkAlreadyFriends(command.args()[FRIEND_NAME]);
            Helpers.checkInFile(command.args()[FRIEND_NAME], creator);
            Helpers.addInformation(updatedInformation(command), creator);

        } catch (FriendNotRegisteredException | AlreadyFriendsException |
                 AddYourselfException ee) {
            ExceptionFormater.exceptionAdd(command.line(), ee.getLocalizedMessage(), ee.getStackTrace(),
                exceptionDirectory);
            return ee.getLocalizedMessage();

        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Add friend directory mistake", e.getStackTrace(),
                exceptionDirectory);
            throw new RuntimeException("Could not add the friend successfully", e);
        }

        return String.format("Friend %s is added.", command.args()[FRIEND_NAME]);
    }

    private List<String> updatedInformation(Command command) throws IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String lineRead;
            List<String> lines = new ArrayList<>();
            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");
                if (splitedUser[USER].equals(command.line())) {
                    lines.add(user.addFriend(command.args()[FRIEND_NAME]));
                } else if (splitedUser[USER].equals(command.args()[FRIEND_NAME])) {
                    User userFriend = User.of(lineRead);
                    lines.add(userFriend.addFriend(command.line()));
                } else {
                    lines.add(lineRead);
                }
            }
            return lines;
        }
    }
}

