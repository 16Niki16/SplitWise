package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface SplitGroupNotificationAPI {
    /**
     * append information about splitting money between friends group
     * */
    void appendToGroupSplit(Command command, double amount, String friend);
}
