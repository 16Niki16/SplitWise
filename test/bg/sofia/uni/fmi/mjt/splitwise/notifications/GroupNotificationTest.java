package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.group.PayGroupNotifications;
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

public class GroupNotificationTest {
    private PayGroupNotifications notification;
    private ReaderWriterCreator notificationsDirectory;
    private ReaderWriterCreator tempNotif;
    String notif;

    @BeforeEach
    void setUp() {
        notif = """
            name:niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name:ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";

        notificationsDirectory = mock();
        tempNotif = mock();
        notification = new PayGroupNotifications(notificationsDirectory, tempNotif);
    }

    @Test
    void addNotificationFriendPaymentValid() {
        String notificationTest = notif;
        CommandLine command = new CommandLine("koki", "group-paid", "10", "niki", "firstGroup");
        when(notificationsDirectory.getRead()).thenAnswer(x -> new StringReader(notificationTest));
        when(notificationsDirectory.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notificationsDirectory.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notificationTest));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        notification.appendToGroupPayment(command, "ili");
        verify(notificationsDirectory, times(2)).getRead();
        verify(notificationsDirectory).getNotAppend();
        verify(notificationsDirectory, never()).getAppend();
    }

}
