package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.AddFriendData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.response.AddFriendResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddFriendCommand implements Command<AddFriendData> {
    private final ApplicationServices applicationServices;

    @Override
    public ResponseData execute(User user, AddFriendData addFriendData) {
        UserService userService = applicationServices.getUserService();
        User newFriend = userService.getUserByUsername(addFriendData.friendName());

        if (user.equals(newFriend)) {
            throw new AddYourselfException("You can not add yourself as a friend!");
        }

        user.addFriend(newFriend.getUsername());
        newFriend.addFriend(user.getUsername());

        return AddFriendResponse.of(addFriendData.friendName());

    }
}

