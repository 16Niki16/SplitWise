package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.files.DataStore;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

public class AddFriendCommand implements Command {
    private final DataStore userData;
    private final String friendName;

    public AddFriendCommand(DataStore userData, String friendName) {
        this.userData = userData;
        this.friendName = friendName;
    }

    @Override
    public String execute(User user) {
        checkAddYourself(user.getUsername(), friendName);
        User newFriend = userData.getUser(friendName);
        user.addFriend(newFriend.getUsername());
        newFriend.addFriend(user.getUsername());

        return String.format("Friend %s is added.", newFriend.getUsername());

    }

    private void checkAddYourself(String name, String addName) {
        if (name.equals(addName)) {
            throw new AddYourselfException("You can not add yourself as a friend!");
        }
    }
}

