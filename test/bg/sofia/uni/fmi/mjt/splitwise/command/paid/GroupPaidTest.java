package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupPaidTest {
    private PaidGroupAPI paid;
    private String groups;
    private String notif;
    private String friend;

    @BeforeEach
    void setUp() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
        String except = "";
        notif = """
            name:niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name:ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";
        groups = """
            firstGroup|niki123 0.00,niki 0.00,kolio 0.00
            secondGroup|niki123 10.00,niki 10.00,kolio -5.00""";
        friend = """
            niki123|Kiro123|kolio 3.67,niki 1.67|BGN
            niki|Deni123|niki123 -5.11|EUR
            kolio|User123|niki123 -5.53|USD""";

        User user = User.of("niki|niki123|niki123 5.12|EUR");

        ReaderWriterCreator notifications = mock();
        ReaderWriterCreator group = mock();
        ReaderWriterCreator tempNotif = mock();
        ReaderWriterCreator friends = mock();
        ExchangeRate rate = mock();

        paid = new PaidGroup(group, notifications, tempNotif, friends, user, rate);

        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groups));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());

        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());

        Map<String, Double> currencies = new HashMap<>();
        currencies.put("EUR", 0.92786);
        currencies.put("BGN", 1.807805);
        when(rate.exchange(any(), any())).thenReturn(currencies);
    }

    @Test
    void testCreateGroupValid() {
        CommandLine command = CommandCreator.newCommand("niki paid-group 10 niki123 secondGroup");
        assertEquals(paid.personPaidToGroup(command), "Successful payment in a group!", "failed test pay in group.");
    }

    @Test
    void testGroupDoesNotExistException(){
        CommandLine command = CommandCreator.newCommand("niki paid-group 10 niki123 unknownGroup");
        assertEquals(paid.personPaidToGroup(command), "Group with this name does not exist!", "failed test pay in group.");
    }

    @Test
    void testPersonNotAFriendException(){
        CommandLine command = CommandCreator.newCommand("niki paid-group 10 kolio secondGroup");
        assertEquals(paid.personPaidToGroup(command), "You are not still friends", "failed test pay in group.");
    }
}
