package bg.sofia.uni.fmi.mjt.splitwise.response;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("login")
public record LoginResponse(ResponseStatus responseType, List<String> notifications, String token)
    implements ResponseData {
    public static LoginResponse of(List<Notification> notifications, String token) {
        List<String> notificationMessages = notifications.stream()
            .map(Notification::getNotification)
            .toList();
        return new LoginResponse(ResponseStatus.SUCCESSFUL, notificationMessages, token);
    }

    @Override
    public String getResponse() {
        StringBuilder buildResponse = new StringBuilder("Your notifications are:\n");
        notifications.forEach(notification -> buildResponse.append(notification).append('\n'));
        return buildResponse.toString();
    }
}
