package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface PayGroupNotificationsAPI {
    /**
     * append information about payment made in a group
     * */
    void appendToGroupPayment(Command command, String friend);
}
