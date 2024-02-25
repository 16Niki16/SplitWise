package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandExecutorTest {
    private static final String RANDOM = "random";
    @Mock
    private ExchangeRate client;
    @Mock
    private User user;
    @Mock
    private Command command;
    @InjectMocks
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new CommandExecutor(RANDOM, RANDOM, RANDOM, RANDOM, RANDOM);
    }

    @Test
    void testExecuteHelp() {
        when(command.args()).thenReturn(new String[] {"help"});

        assertNotNull(command.args());
        assertTrue(
            executor.execute(command, user, client)
                .contains("create-group <group_name> <username> <username> ... <username>"));
    }

}
