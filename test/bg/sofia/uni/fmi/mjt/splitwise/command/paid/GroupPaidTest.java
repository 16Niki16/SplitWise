package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupPaidTest {
    private PaidGroupAPI paid;
    private ReaderWriterCreator notifications;
    private ReaderWriterCreator group;
    private ReaderWriterCreator exc;
    private ReaderWriterCreator tempNotif;
    private String groups;
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
        groups = """
            firstGroup | niki123 0.00, niki 0.00, kolio 0.00
            secondGroup | niki123 -20.00, niki 10.00, pepi 10.00""";

        notifications = mock();
        group = mock();
        exc = mock();
        tempNotif = mock();
        paid = new PaidGroup(group, notifications, exc, tempNotif);
    }

    @Test
    void testCreateGroupValid() {
        String groupTest = groups;
        Command command = CommandCreator.newCommand("niki paid-group 10 pepi secondGroup");
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groupTest));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(paid.personPaidToGroup(command), "Successful payment in a group!", "failed test pay in group.");
    }

    @Disabled
    void testCreateGroupNotValid() {
        String groupTest = groups;
        Command command = CommandCreator.newCommand("niki group-paid 20 pepi secondGroup");
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groupTest));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(paid.personPaidToGroup(command), "Number format is not correct", "failed test pay in group.");
    }
}
