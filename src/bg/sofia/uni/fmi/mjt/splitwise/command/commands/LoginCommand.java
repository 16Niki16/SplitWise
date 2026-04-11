package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LoginCommand implements Command<LoginData> {
    private ApplicationServices applicationServices;
    private SessionsManager sessionsManager;

    @Override
    public Response execute(String token, LoginData loginData) {
        UserService userService = applicationServices.getUserService();

        User user = userService.getUserByUsername(loginData.username());
        if (!user.getPassword().equals(loginData.password())) {
            throw new PasswordNotCorrectException("The provided password is not correct!");
        }
        String newToken = sessionsManager.createSession(user);

        NotificationsService notificationsService = applicationServices.getNotificationsService();
        List<Notification> notifications = notificationsService.getUserNotifications(user);

        return new Response(newToken, LoginResponse.of(notifications));
    }

}
