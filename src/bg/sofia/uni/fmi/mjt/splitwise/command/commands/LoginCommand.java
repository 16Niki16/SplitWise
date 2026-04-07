package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LoginCommand implements Command {
    private String username;
    private String password;
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        if (!user.getPassword().equals(password)) {
            throw new PasswordNotCorrectException("The provided password is not correct!");
        }
        NotificationsService notificationsService = applicationServices.getNotificationsService();
        List<Notification> notifications = notificationsService.getUserNotifications(user);
        return notifications.;
    }

}
