package bg.sofia.uni.fmi.mjt.splitwise.response;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;

import java.util.List;

public record LoginResponse(ResponseStatus responseType, List<String> notifications) implements ResponseData {
    public static LoginResponse of(List<Notification> notifications) {
        List<String> notificationMessages = notifications.stream()
                .map(Notification::getNotification)
                .toList();
        return new LoginResponse(ResponseStatus.SUCCESSFUL, notificationMessages);
    }

    @Override
    public String getResponse() {
        StringBuilder buildResponse = new StringBuilder("Your notifications are:\n");
        notifications.forEach(notification -> buildResponse.append(notification).append('\n'));
        return buildResponse.toString();
    }
}
