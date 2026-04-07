package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.repository.NotificationsRepository;

import java.util.List;

public class NotificationsService implements Service {
    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public void addNotification(User user, Notification notification) {
        this.notificationsRepository.addNotification(user.getUsername(), notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return this.notificationsRepository.getNotifications(user.getUsername());
    }
}
