package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusTest {
    private StatusAPI status;
    private ReaderWriterCreator directory;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator exceptions;
    private String direc;
    private String direcGroup;
    private String except;
    private Command command;
    private Command commandEmpty;

    @BeforeEach
    void setUp() {
        direc = """
            niki | niki123 | koki -5.00, kolio -10.00, test -10.00
            ili  | ili123
            kolio | kolio123 | niki 10.00
            koki | koki123 | niki 5.00
            test | test | niki 10.00""";
        direcGroup = """
            firstGroup | niki -0.17, koki 5.33
            testGroup | niki 0.00, kolio 0.00""";
        except = "";
        command = CommandCreator.newCommand("niki get-status");
        commandEmpty = CommandCreator.newCommand("ili get-status");
        directory = mock();
        groupsDirectory = mock();
        exceptions = mock();
        status = new Status(directory, groupsDirectory, exceptions);
    }

    @Test
    void testGetStatusValid() {
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(direcGroup));
        when(directory.getRead()).thenAnswer(x -> new StringReader(direc));
        assertTrue(status.getStatus(command).contains("*koki owes you 5.00 LV."),
            "failed test get-status.");
    }

    @Test
    void testGetStatusEmpty() {
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(direcGroup));
        when(directory.getRead()).thenAnswer(x -> new StringReader(direc));
        assertEquals(status.getStatus(commandEmpty), ("You do not have any friends\n" +
                "You do not participate in any groups"),
            "failed test get-status.");
    }
}
