package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

public class AddFriendCommand implements Command {
    private final UserService userService;
    private final String friendName;

    public AddFriendCommand(UserService userService, String friendName) {
        this.userService = userService;
        this.friendName = friendName;
    }

    @Override
    public String execute(User user) {
        User newFriend = userService.getUserByUsername(friendName);
        if (user.equals(newFriend)) {
            throw new AddYourselfException("You can not add yourself as a friend!");
        }

        user.addFriend(newFriend.getUsername());
        newFriend.addFriend(user.getUsername());
        userService.updateFile();

        return String.format("Friend %s is added.", newFriend.getUsername());

    }
}

