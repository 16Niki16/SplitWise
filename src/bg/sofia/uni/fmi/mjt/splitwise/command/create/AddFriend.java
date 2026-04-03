package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;

public class AddFriend implements AddFriendAPI {
    private final ReaderWriterCreator directory;
    private final User user;

    public AddFriend(ReaderWriterCreator directory, User user) {
        this.directory = directory;
        this.user = user;
    }

    @Override
    public String addingFriend(CommandLine command) {
        try {

            checkAddYourself(command.line(), command.args()[FRIEND_NAME]);
            User userFriend = User.of(Helpers.findFriendLine(command, FRIEND_NAME, directory));
            String appendReceiver = userFriend.addFriend(command.line());

            String appendUser = user.addFriend(command.args()[FRIEND_NAME]);

            Helpers.addInformation(Helpers.updatedInfo(command.line(), command.args()[FRIEND_NAME],
                appendUser, appendReceiver, directory), directory);

            return String.format("Friend %s is added.", command.args()[FRIEND_NAME]);

        } catch (IOException e) {
            throw new RuntimeException("Could not add the friend successfully", e);
        }
    }

    private void checkAddYourself(String name, String addName) {
        if (name.equals(addName)) {
            throw new AddYourselfException("You can not add yourself as a friend!");
        }
    }
}

