package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationTest {
    private SplitPersonNotificationsAPI notification;
    private ReaderWriterCreator notificationsDirectory;
    private ReaderWriterCreator tempNotif;
    private String notif;

    @BeforeEach
    void setUp() {
        notif = """
            name: niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name: ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";

        notificationsDirectory = mock();
        tempNotif = mock();
        notification = new SplitPersonNotifications(notificationsDirectory, tempNotif);
    }

    @Test
    void addNotificationFriendSplitValid() {
        String notificationTest = notif;
        Command command = new Command("koki", "split", "10", "niki", "qjca");
        when(notificationsDirectory.getRead()).thenAnswer(x -> new StringReader(notificationTest));
        when(notificationsDirectory.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notificationsDirectory.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notificationTest));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        notification.addNotificationFriendSplit(command);
        verify(notificationsDirectory, times(2)).getRead();
        verify(notificationsDirectory).getNotAppend();
        verify(notificationsDirectory, never()).getAppend();
    }
}
