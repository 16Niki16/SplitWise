package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
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
    private String groups;
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
        groups = """
            firstGroup | niki123 0.00, niki 0.00, kolio 0.00
            secondGroup | niki123 -20.00, niki 10.00, pepi 10.00""";

        notifications = mock();
        group = mock();
        paid = new PaidGroup(group, group);
    }

    @Test
    void testCreateGroupValid() {
        String groupTest = groups;
        Command command = CommandCreator.newCommand("niki group-paid 10 pepi secondGroup");
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(group.getRead()).thenAnswer(x -> new StringReader(groupTest));
        when(group.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(group.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(paid.personPaidToGroup(command), "Successful payment in a group!", "failed test pay in group.");
    }
}
