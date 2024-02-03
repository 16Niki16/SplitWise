package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface NotificationAPI {
    /**
     * add information about payment
     * */
    void addNotificationFriendPayment(Command command);

    /**
     * add information about split
     * */
    void addNotificationFriendSplit(Command command);
}
