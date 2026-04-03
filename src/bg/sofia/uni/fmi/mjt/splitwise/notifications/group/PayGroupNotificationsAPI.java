package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;

public interface PayGroupNotificationsAPI {
    /**
     * append information about payment made in a group
     * */
    void appendToGroupPayment(CommandLine command, String friend);
}
