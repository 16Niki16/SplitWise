package bg.sofia.uni.fmi.mjt.splitwise.notifications.user;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface PersonPayNotificationsAPI {
    /**
     * add information about payment
     */
    void addNotificationFriendPayment(Command command);
}

