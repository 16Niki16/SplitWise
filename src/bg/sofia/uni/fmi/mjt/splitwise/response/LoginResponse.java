package bg.sofia.uni.fmi.mjt.splitwise.response;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;

import java.util.List;

public record LoginResponse(ResponseStatus responseType, List<Notification> notifications) implements Response {
    public static LoginResponse of(List<Notification> notifications) {
        return new LoginResponse(ResponseStatus.SUCCESSFUL, notifications);
    }
}
