package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddFriendCommand implements Command {
    private final String friendName;
    private final ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        UserService userService = applicationServices.getUserService();
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

