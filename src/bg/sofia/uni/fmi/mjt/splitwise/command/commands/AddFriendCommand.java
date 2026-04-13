package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.AddFriendData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.response.AddFriendResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddFriendCommand implements Command<AddFriendData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, AddFriendData addFriendData) {
        UserService userService = applicationServices.getUserService();
        User user = sessionsManager.getUserSession(token);
        User newFriend = userService.getUserByUsername(addFriendData.friendName());

        user.addFriend(newFriend.getUsername());
        newFriend.addFriend(user.getUsername());

        return AddFriendResponse.of(addFriendData.friendName());

    }
}

