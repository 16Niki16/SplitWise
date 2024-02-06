package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SplitTest {
    private Split split;
    private ReaderWriterCreator friends;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator exc;
    private ReaderWriterCreator tempNotif;
    private User user;
    private String friend;
    private String notif;
    private String except;

    @BeforeEach
    void setUp() {
        except = "";
        notif = """
            name: niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name: ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";
        friend = """
            niki | niki123 | pepi 10.00, ili 0.00, koki 5.00
            kolio | kolio123 | pepi 0.00
            pepi | pepi123 | niki -10.00""";
        user = User.of("niki | niki123 | pepi 10.00, kolio 0.00, ili 0.00, koki 5.00");
        friends = mock();
        notifications = mock();
        exc = mock();
        tempNotif = mock();
        split = new Split(friends, user, notifications, exc, tempNotif);
    }

    @Test
    void testMoneyOweValid() {
        String testFriends = friend;
        Command command = CommandCreator.newCommand("niki split 20 pepi qjca");
        when(friends.getRead()).thenAnswer(x -> new StringReader(testFriends));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(split.moneyOwe(command), "Successfully split the money!", "failed test split.");
    }

    @Test
    void testMoneyOweExceptions() {
        String testFriends = friend;
        Command command = CommandCreator.newCommand("niki split 20 kolio qjca");
        when(friends.getRead()).thenAnswer(x -> new StringReader(testFriends));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(split.moneyOwe(command), "You are not still friends", "failed test split not valid.");
    }

}
