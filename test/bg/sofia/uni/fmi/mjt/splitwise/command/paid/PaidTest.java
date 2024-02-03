package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.Split;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaidTest {
    private PaidAPI paid;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator friend;
    private User user;
    private String friends;
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
        friends = """
            niki | niki123 | pepi 10.00, ili 0.00, koki 5.00
            kolio | kolio123 | pepi 0.00
            pepi | pepi123 | niki -10.00""";
        user = User.of("niki | niki123 | pepi 10.00, kolio 0.00, ili 0.00, koki 5.00");
        friend = mock();
        notifications = mock();
        paid = new Paid(friend, user, notifications);
    }

    @Test
    void testMoneyOweValid() {
        String testFriends = friends;
        Command command = CommandCreator.newCommand("niki paid 10 pepi");
        when(friend.getRead()).thenAnswer(x -> new StringReader(testFriends));
        when(friend.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friend.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(paid.personPay(command),"Successfully paid!", "failed test pay.");
    }
}
