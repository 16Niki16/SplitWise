package bg.sofia.uni.fmi.mjt.splitwise.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandCreatorTest {

    private Command command;

    @BeforeEach
    void setUp() {
        command = new Command("kiro", "split", "20", "niki", "gas");
    }

    @Test
    void testCommandCreator() {
        Command testCommand = CommandCreator.newCommand("kiro split 20 niki gas");
        assertEquals(command.line(),testCommand.line());
    }
}
