package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class RequestHandlerTest {

    private static final String TOKEN = "dummy token";
    @Mock
    private Request request;
    @Mock
    private CommandRegistry commandRegistry;
    @Mock
    private SessionsManager sessionsManager;
    @Mock
    private UserService userService;
    @Mock
    private Command command;
    @InjectMocks
    private RequestHandler requestHandler;

    @Test
    void testLoginRequest() {
        LoginData data = new LoginData("Niki", "Niki123!");
        when(commandRegistry.create(request)).thenReturn(command);

        when(request.data()).thenReturn(data);
        User user = new User("Niki", "Niki123!", null, null, "EUR");
        when(userService.getUserByUsername(data.username())).thenReturn(user);
        when(sessionsManager.createSession(user)).thenReturn(TOKEN);

        ResponseData responseData = mock();
        when(command.execute(user)).thenReturn(responseData);

        Response response = requestHandler.handle(request);

        assertEquals(TOKEN, response.token());
        assertEquals(responseData, response.responseData());

        verify(sessionsManager).createSession(user);
    }

    @Test
    void testCreateAccount() {
        CreateAccountData data = new CreateAccountData("Niki", "Niki123!");
        when(commandRegistry.create(request)).thenReturn(command);

        when(request.data()).thenReturn(data);

        ResponseData responseData = mock();
        when(command.execute(any())).thenReturn(responseData);

        Response response = requestHandler.handle(request);

        assertNull(response.token());
        assertEquals(responseData, response.responseData());
    }
}
