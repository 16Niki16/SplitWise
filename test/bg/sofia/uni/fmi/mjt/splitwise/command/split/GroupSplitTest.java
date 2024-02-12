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
    private ReaderWriterCreator exc;
    private ReaderWriterCreator tempNotif;
    private String notif;
    private String groups;
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
            firstGroup|niki123 0.00,niki 0.00,kolio 0.00
            secondGroup|niki123 0.00,niki 0.00,kolio 0.00""";
        groupsDirectory = mock();
        notifications = mock();
        exc = mock();
        tempNotif = mock();
        split = new GroupSplit(groupsDirectory, notifications, exc, tempNotif);
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
        when(tempNotif.getRead()).thenAnswer(x -> new StringReader(notif));
        when(tempNotif.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(tempNotif.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(split.groupsOwe(command), "Information successfully added", "failed test split group.");
    }

    @Test
    void testGroupsOweException() {
        String testGroups = groups;
        Command command = CommandCreator.newCommand("niki split-group 18 unknown qjca");
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(testGroups));
        when(groupsDirectory.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(groupsDirectory.getAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getRead()).thenAnswer(x -> new StringReader(notif));
        when(notifications.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(notifications.getAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(split.groupsOwe(command), "Group with that name does not exist",
            "failed test split group invalid.");
    }
}
