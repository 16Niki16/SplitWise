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

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class AddFriend implements AddFriendAPI {
    private ReaderWriterCreator directory;
    private User user;
    private ReaderWriterCreator exceptionDirectory;
    private ClientContainer container;

    public AddFriend(ReaderWriterCreator directory, User user, ReaderWriterCreator exceptionDirectory,
                     ClientContainer container) {
        this.directory = directory;
        this.user = user;
        this.exceptionDirectory = exceptionDirectory;
        this.container = container;
    }

    @Override
    public String addingFriend(Command command) {
        try {

            ExceptionHandler.checkAddYourself(command.line(), command.args()[FRIEND_NAME]);
            User userFriend = User.of(friendLine(command));
            String appendReceiver = userFriend.addFriend(command.line());

            this.user.checkAlreadyFriends(command.args()[FRIEND_NAME]);
            String appendUser = user.addFriend(command.args()[FRIEND_NAME]);

            Helpers.addInformation(Helpers.updatedInfo(command.line(), command.args()[FRIEND_NAME],
                    appendUser, appendReceiver, directory), directory);

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

    private String friendLine(Command command)
        throws IOException, FriendNotRegisteredException {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] splited = line.split("\\|");
                if (splited[USER].equals(command.args()[FRIEND_NAME])) {
                    return line;
                }
            }
        }
        throw new FriendNotRegisteredException("This person is still not registered!");
    }
}

