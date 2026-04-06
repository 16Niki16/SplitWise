package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.StatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusTest {
    private StatusAPI status;
    private ReaderWriterCreator groupsDirectory;
    private String direcGroup;
    private CommandLine command;
    private CommandLine commandEmpty;

    @BeforeEach
    void setUp() {
        direcGroup = """
            firstGroup | niki -0.17, koki 5.33
            testGroup | niki 0.00, kolio 0.00""";
        command = CommandCreator.newCommand("niki get-status");
        commandEmpty = CommandCreator.newCommand("ili get-status");
        groupsDirectory = mock();
        User user = mock();
        ExchangeRate client = mock();
        status = new StatusCommand( groupsDirectory, user, client);
    }

    @Disabled
    void testGetStatusValid() {
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(direcGroup));
        assertTrue(status.getStatus(command).contains("*koki owes you 5.00 LV."),
            "failed test get-status.");
    }

    @Disabled
    void testGetStatusEmpty() {
        when(groupsDirectory.getRead()).thenAnswer(x -> new StringReader(direcGroup));
        assertEquals(status.getStatus(commandEmpty), ("You do not have debts with friends\n" +
                "You do not have debts in the groups!"),
            "failed test get-status.");
    }
}
