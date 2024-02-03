package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupSplitTest {
    private GroupSplit split;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator notifications;
    private String notif;
    private String groups;

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
            secondGroup | niki123 0.00, niki 0.00, kolio 0.00""";
        groupsDirectory = mock();
        notifications = mock();
        split = new GroupSplit(groupsDirectory, notifications);
    }

    @Test
    void testGroupsOweValid() {
        String testGroups = groups;
        Command command = CommandCreator.newCommand("niki split-group 18 firstGroup qjca");
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(testGroups));
        when(groupsDirectory.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(groupsDirectory.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(split.groupsOwe(command),"Information successfully added", "failed test split group.");
    }

}
