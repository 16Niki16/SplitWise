package bg.sofia.uni.fmi.mjt.splitwise.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineCreatorTest {

    private CommandLine command;

    @BeforeEach
    void setUp() {
        command = new CommandLine("kiro", "split", "20", "niki", "gas");
    }

    @Test
    void testCommandCreator() {
        CommandLine testCommand = CommandCreator.newCommand("kiro split 20 niki gas");
        assertEquals(command.line(), testCommand.line());
    }
}
