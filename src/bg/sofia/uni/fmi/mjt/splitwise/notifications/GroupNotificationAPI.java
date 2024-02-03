package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface GroupNotificationAPI {
    /**
     * append information about splitting money between friends group
     * */
    void appendToGroupSplit(Command command, String friend, String amount);

    /**
     * append information about payment made in a group
     * */
    void appendToGroupPayment(Command command, String friend);
}
