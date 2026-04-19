package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateAccountCommand;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UsernameAlreadyUsedException;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateAccountResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseStatus;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateAccoundCommandTest {
    private static final String DUMMY = "dummy";
    private static final String CORRECT_PASSWORD = "Correct123!";
    private static final String MESSAGE = "You successfully created account with username: ";
    @Mock
    private ApplicationServices applicationServices;
    @Mock
    private SessionsManager sessionsManager;
    @Mock
    private UserService userService;
    @InjectMocks
    private CreateAccountCommand createAccountCommand;


    @Test
    void testCreteAccountCorrectCredentials() {
        UserService userService = mock();
        CreateAccountData data = new CreateAccountData(DUMMY, CORRECT_PASSWORD);

        when(applicationServices.getUserService()).thenReturn(userService);

        ResponseData response = createAccountCommand.execute(DUMMY, data);
        CreateAccountResponse createAccountResponse = (CreateAccountResponse) response;

        assertEquals(ResponseStatus.SUCCESSFUL, createAccountResponse.responseStatus());
        assertEquals(createAccountResponse.getResponse(), MESSAGE + DUMMY);
    }

    @Test
    void testIncorrectPasswordFormat() {
        CreateAccountData data = new CreateAccountData(DUMMY, DUMMY);

        when(applicationServices.getUserService()).thenReturn(userService);

        assertThrows(PasswordNotCorrectException.class, () -> createAccountCommand.execute(DUMMY, data));
    }

    @Test
    void testUserAlreadyExists() {
        CreateAccountData data = new CreateAccountData(DUMMY, DUMMY);

        when(applicationServices.getUserService()).thenReturn(userService);
        when(userService.checkUserExists(any())).thenReturn(true);

        assertThrows(UsernameAlreadyUsedException.class, () -> createAccountCommand.execute(DUMMY, data));
    }
}
