package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PayGroupNotifications implements PayGroupNotificationsAPI {
    private static final int GROUP_PAYMENT = 3;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public PayGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    public void appendToGroupPayment(Command command, String friend) {
        try {
            String message = String.format("*%s - %s approved your payment %.2f LV.", command.args()[GROUP_PAYMENT],
                    command.line(), Double.parseDouble(command.args()[AMOUNT]));

            HelpersNotifications.appendAtPosition(friend, message, notificationsDirectory);
            HelpersNotifications.appendAtPosition(friend, message, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
