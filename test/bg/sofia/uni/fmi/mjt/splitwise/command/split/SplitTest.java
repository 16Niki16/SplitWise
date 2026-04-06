package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
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

public class SplitTest {
    private SplitCommand split;
    private String friend;
    private String notif;
    private String except;
    ExchangeRate rate;

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
        ReaderWriterCreator friends = mock();
        ReaderWriterCreator notifications = mock();
        ReaderWriterCreator tempNotif = mock();
        rate = mock();
        split = new SplitCommand(friends, user, notifications, tempNotif, rate);

        when(friends.getRead()).thenAnswer(x -> new StringReader(friend));
        when(friends.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(friends.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        Map<String, Double> currencies = new HashMap<>();
        currencies.put("EUR", 0.92786);
        currencies.put("BGN", 1.807805);
        when(rate.exchange(any(), any())).thenReturn(currencies);
    }

    @Test
    void testMoneyOweValid() {
        CommandLine command = CommandCreator.newCommand("niki split 20 pepi qjca");
        assertEquals(split.moneyOwe(command), "Successfully split the money!", "failed test split.");
    }

    @Test
    void testMoneyOweExceptions() {
        CommandLine command = CommandCreator.newCommand("niki split 20 kolio qjca");
        assertEquals(split.moneyOwe(command), "You are not still friends", "failed test split not valid.");
    }

    @Test
    void testPersonNotRegisteredYet() {
        CommandLine command = CommandCreator.newCommand("niki split 20 ili qjca");
        assertEquals(split.moneyOwe(command), "This person is still not registered!", "failed test split not valid.");
    }

    @Test
    void unknownCurrencyException() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
        CommandLine command = CommandCreator.newCommand("niki split 20 pepi qjca");
        UnknownCurrencyException currencyException = new UnknownCurrencyException("Unknown currency!");
        when(rate.exchange(any(), any())).thenThrow(currencyException);
        assertEquals(split.moneyOwe(command), "Unknown currency!", "failed test split not valid.");
    }
}
