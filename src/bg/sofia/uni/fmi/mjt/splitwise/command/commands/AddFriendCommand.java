package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

public class AddFriendCommand implements Command {
    private final String friendName;

    public AddFriendCommand(String friendName) {
        this.friendName = friendName;
    }

    @Override
    public String execute(User user) {
        try {
            checkAddYourself(user.getUsername(), friendName);
            User userFriend = User.of(Helpers.findFriendLine(friendName));
            userFriend.addFriend(user.getUsername());
            user.addFriend(userFriend.getUsername());

            /*Helpers.addInformation(Helpers.updatedInfo(command.line(), command.args()[FRIEND_NAME],
                appendUser, appendReceiver, directory), directory);*/

            return String.format("Friend %s is added.", userFriend.getUsername());

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

