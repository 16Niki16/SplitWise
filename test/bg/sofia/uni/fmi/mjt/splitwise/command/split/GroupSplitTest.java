package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.GroupSplitCommand;
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

public class GroupSplitTest {
    private GroupSplitCommand split;
    private String notif;
    private String groups;
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

        groups = """
            firstGroup|niki123 0.00,niki 0.00,kolio 0.00
            secondGroup|niki123 0.00,niki 0.00,kolio 0.00""";

        User user = User.of("niki|niki123|pepi 10.00,kolio 0.00,ili 0.00,koki 5.00|EUR");

        ReaderWriterCreator groupsDirectory = mock();
        ReaderWriterCreator notifications = mock();
        ReaderWriterCreator tempNotif = mock();
        rate = mock();
        split = new GroupSplitCommand(groupsDirectory, notifications, tempNotif, user, rate);

        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(groups));
        when(groupsDirectory.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(groupsDirectory.getAppend()).thenAnswer(x -> new StringWriter());
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
    void testGroupsOweValid() {
        CommandLine command = CommandCreator.newCommand("niki split-group 18 firstGroup qjca");
        assertEquals(split.groupsOwe(command), "Information successfully added", "failed test split group.");
    }

    @Test
    void testGroupsOweException() {
        CommandLine command = CommandCreator.newCommand("niki split-group 18 unknown qjca");
        assertEquals(split.groupsOwe(command), "Group with this name does not exist!",
            "failed test split group invalid.");
    }

    @Test
    void unknownCurrencyException() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
        CommandLine command = CommandCreator.newCommand("niki split-group 18 firstGroup qjca");
        UnknownCurrencyException currencyException = new UnknownCurrencyException("Unknown currency!");
        when(rate.exchange(any(), any())).thenThrow(currencyException);
        assertEquals(split.groupsOwe(command), "Unknown currency!", "failed test split not valid.");
    }
}
