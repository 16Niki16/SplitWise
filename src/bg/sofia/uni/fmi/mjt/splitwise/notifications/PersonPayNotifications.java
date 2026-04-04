package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PersonPayNotifications implements Notification {
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;
    public PersonPayNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendPayment(CommandLine command) {
        try {
            String message = String.format("%s approved your payment %.2f LV.", command.line(),
                    Double.parseDouble(command.args()[AMOUNT]));
            HelpersNotifications.appendNotification(command, message, notificationsDirectory);

            HelpersNotifications.appendNotification(command, message, tempNotif);

        } catch (IOException e) {
            //ExceptionFormater.exceptionAdd(command.line(),"IO exception in notifications", e.getStackTrace());
            throw new RuntimeException(e);
        }

    }

}
