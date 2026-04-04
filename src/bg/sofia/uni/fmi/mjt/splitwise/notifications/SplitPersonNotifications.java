package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class SplitPersonNotifications implements Notification {
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public SplitPersonNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendSplit(CommandLine command) {
        try {
            String message = String.format("You owe %s %.2f LV[%s]",
                    command.line(), Double.parseDouble(command.args()[AMOUNT]) / TWO, Helpers.getReason(command));

            HelpersNotifications.appendNotification(command, message, notificationsDirectory);

            HelpersNotifications.appendNotification(command, message, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
