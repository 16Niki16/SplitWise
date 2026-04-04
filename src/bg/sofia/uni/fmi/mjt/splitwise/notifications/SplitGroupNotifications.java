package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

public class SplitGroupNotifications implements Notification {
    private static final int GROUP_NAME = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public SplitGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void appendToGroupSplit(CommandLine command, double amount, String friend) {
        try {
            String message = String.format("*%s - You owe %s %.2f LV[%s]",
                    command.args()[GROUP_NAME], command.line(), amount, Helpers.getReason(command));
            HelpersNotifications.appendAtPosition(friend, message, notificationsDirectory);

            HelpersNotifications.appendAtPosition(friend, message, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
