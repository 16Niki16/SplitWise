package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LoginCommand implements Command {
    private Data data;
    private ApplicationServices applicationServices;

    @Override
    public ResponseData execute(User user) {
        if (!(data instanceof LoginData loginData)) {
            throw new DataException("Problem in Login data");
        }

        if (!user.getPassword().equals(loginData.password())) {
            throw new PasswordNotCorrectException("The provided password is not correct!");
        }

        NotificationsService notificationsService = applicationServices.getNotificationsService();
        List<Notification> notifications = notificationsService.getUserNotifications(user);
        return LoginResponse.of(notifications);
    }

}
