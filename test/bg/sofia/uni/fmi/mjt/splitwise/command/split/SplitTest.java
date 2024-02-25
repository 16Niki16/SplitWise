package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
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
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SplitTest {
    private Split split;
    private ReaderWriterCreator friends;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator exc;
    private ReaderWriterCreator tempNotif;
    private String friend;
    private String notif;
    private String except;

    @BeforeEach
    void setUp() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
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
        friend = """
            niki|niki123|pepi 10.00,ili 0.00,koki 5.00|BGN
            kolio|kolio123|pepi 0.00|BGN
            pepi|pepi123|niki -10.00|EUR""";
        User user = User.of("niki|niki123|pepi 10.00,kolio 0.00,ili 0.00,koki 5.00|BGN");
        friends = mock();
        notifications = mock();
        exc = mock();
        tempNotif = mock();
        ExchangeRate rate = mock();
        split = new Split(friends, user, notifications, exc, tempNotif, rate);

        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
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
        Map<String, Double> currencies = new HashMap<>();
        currencies.put("EUR", 0.92786);
        currencies.put("BGN", 1.807805);
        when(rate.exchange(any(), any())).thenReturn(currencies);
    }

    @Test
    void testMoneyOweValid() {
        Command command = CommandCreator.newCommand("niki split 20 pepi qjca");
        assertEquals(split.moneyOwe(command), "Successfully split the money!", "failed test split.");
    }

    @Test
    void testMoneyOweExceptions() {
        Command command = CommandCreator.newCommand("niki split 20 kolio qjca");
        assertEquals(split.moneyOwe(command), "You are not still friends", "failed test split not valid.");
    }

}
