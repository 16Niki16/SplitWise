package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
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

public class PaidTest {
    private PaidAPI paid;
    private String friends;
    private String notif;
    private String except;
    private String tempNotif;

    @BeforeEach
    void setUp() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
        tempNotif = """
            name:niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name:ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";
        except = "";
        notif = """
            name:niki
            Friends:
            koki approved your payment 5 LV.
            Groups:
            *testGroup - koki approved your payment 2 LV.
            name:ili
            Groups:
            *testGroup - You owes koki 3.3333333333333335 LV[qjca]""";
        friends = """
            niki|niki123|pepi 10.00,ili 0.00,koki 5.00|BGN
            kolio|kolio123|pepi 0.00|EUR
            pepi|pepi123|niki -10.00|BGN""";
        User user = User.of("niki|niki123|pepi 10.00,ili 0.00,koki 5.00|BGN");
        ReaderWriterCreator friend = mock();
        ReaderWriterCreator notifications = mock();
        ReaderWriterCreator temp = mock();
        ExchangeRate rate = mock();
        paid = new PaidCommand(friend, user, notifications, temp, rate);

        when(friend.getRead()).thenAnswer(x -> new StringReader(friends));
        when(friend.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friend.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(temp.getRead()).thenAnswer(x -> new StringReader(tempNotif));
        when(temp.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(temp.getAppend()).thenAnswer(x -> new StringWriter());
        Map<String, Double> currencies = new HashMap<>();
        currencies.put("EUR", 0.92786);
        currencies.put("BGN", 1.807805);
        when(rate.exchange(any(), any())).thenReturn(currencies);
    }

    @Test
    void testMoneyOweValid() {
        CommandLine command = CommandCreator.newCommand("niki paid 10 pepi");
        assertEquals(paid.personPay(command), "Successfully paid!", "failed test pay.");
    }

    @Test
    void testTestNotFriends() {
        CommandLine command = CommandCreator.newCommand("niki paid 10 kolio");
        assertEquals(paid.personPay(command), "Unsuccessful payment!", "Unsuccessful check for friends!");
    }

    @Test
    void testNotRegistered() {
        CommandLine command = CommandCreator.newCommand("niki paid 10 ili");
        assertEquals(paid.personPay(command), "Unsuccessful payment!", "Unsuccessful check for friends!");
    }

}
