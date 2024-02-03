package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public class GroupNotification implements GroupNotificationAPI {

    private String notificationDirectory;

    public GroupNotification(String notificationDirectory) {
        this.notificationDirectory = notificationDirectory;
    }

    public void appendToGroups(Command command) {

    }
}
