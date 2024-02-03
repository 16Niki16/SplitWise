package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandExecutorTest {
    private static final int COMMAND = 0;
    private static final String RANDOM = "random";
    @Mock
    private User user;
    @Mock
    private Command command;
    @InjectMocks
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new CommandExecutor(RANDOM, RANDOM, RANDOM);
    }

    @Test
    void testExecuteHelp() {
        when(command.args()).thenReturn(new String[] {"help"}); // Stub the behavior for args()

        assertNotNull(command.args()); // Ensure args() is not null before accessing it
        assertTrue(
            executor.execute(command, user).contains("create-group <group_name> <username> <username> ... <username>"));
    }

    @Test
    void testExecuteUnknown() {
        when(command.args()).thenReturn(new String[] {"unknown"}); // Stub the behavior for args()

        assertNotNull(command.args()); // Ensure args() is not null before accessing it
        assertEquals(executor.execute(command, user), "Unknown command");
    }

}
