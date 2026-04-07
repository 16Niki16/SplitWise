package bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;

import java.util.List;
import java.util.Map;

public record NotificationsWrapper(Map<String, List<Notification>> notifications) {
}
