package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LoginCommand implements Command {
    private Data data;
    private ApplicationServices applicationServices;

    @Override
    public Response execute(User user) {
        if (!(data instanceof LoginData loginData)) {
            throw new DataException("Problem in Login data");
        }

        UserService userService = applicationServices.getUserService();
        User userProfile = userService.getUserByUsername(loginData.username());
        if (!userProfile.getPassword().equals(loginData.password())) {
            throw new PasswordNotCorrectException("The provided password is not correct!");
        }
        NotificationsService notificationsService = applicationServices.getNotificationsService();
        List<Notification> notifications = notificationsService.getUserNotifications(user);
        return LoginResponse.of(notifications);
    }

}
