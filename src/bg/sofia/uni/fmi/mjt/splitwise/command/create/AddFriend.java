package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionHandler;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;

public class AddFriend implements AddFriendAPI {
    private final ReaderWriterCreator directory;
    private final User user;
    private final ReaderWriterCreator exceptionDirectory;

    public AddFriend(ReaderWriterCreator directory, User user, ReaderWriterCreator exceptionDirectory) {
        this.directory = directory;
        this.user = user;
        this.exceptionDirectory = exceptionDirectory;
    }

    @Override
    public String addingFriend(Command command) {
        try {

            ExceptionHandler.checkAddYourself(command.line(), command.args()[FRIEND_NAME]);
            User userFriend = User.of(Helpers.findFriendLine(command, FRIEND_NAME, directory));
            String appendReceiver = userFriend.addFriend(command.line());

            this.user.checkAlreadyFriends(command.args()[FRIEND_NAME]);
            String appendUser = user.addFriend(command.args()[FRIEND_NAME]);

            Helpers.addInformation(Helpers.updatedInfo(command.line(), command.args()[FRIEND_NAME],
                    appendUser, appendReceiver, directory), directory);

            return String.format("Friend %s is added.", command.args()[FRIEND_NAME]);
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
    }

}

