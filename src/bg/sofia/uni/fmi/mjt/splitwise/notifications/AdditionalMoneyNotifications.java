package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;

public class AdditionalMoneyNotifications {
    public static void additionalNotifications(ReaderWriterCreator notifications,
                                               ReaderWriterCreator temporaryNotifications, String message,
                                               String user) {
        try {
            if (HelpersNotifications.isSectionExistNotification(user, notifications)) {

            } else {

            }
            if (HelpersNotifications.isSectionExistNotification(user, temporaryNotifications)) {

            } else {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendInformation(ReaderWriterCreator notifications, String message, String user) throws IOException {
        Helpers.appendToFile(String.format("name: %s\n%s", user, message), notifications);
    }

}
