package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandType;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateAccountResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class RequestHandlerTest {

    private static final String TOKEN = "dummy token";
    private static final String TOKEN_MISSING = null;
    @Mock
    private CommandRegistry commandRegistry;
    @InjectMocks
    private RequestHandler requestHandler;

    @Test
    void testHandleSuccess() {
        Request request = mock();
        Command command = mock();
        LoginResponse responseData = new LoginResponse(ResponseStatus.SUCCESSFUL, null, TOKEN);
        Data data = mock();

        when(request.token()).thenReturn(TOKEN_MISSING);
        when(request.data()).thenReturn(data);
        when(commandRegistry.create(request)).thenReturn(command);
        when(command.execute(any(), any())).thenReturn(responseData);

        Response response = requestHandler.handle(request);

        assertEquals(TOKEN, response.token());
        assertEquals(responseData, response.responseData());

        verify(commandRegistry).create(request);
        verify(command).execute(any(), any());
    }

    @Test
    void testCreateAccount() {
        Request request = mock();
        Command command = mock();
        CreateAccountResponse responseData = mock();
        Data data = mock();

        when(request.token()).thenReturn(TOKEN_MISSING);
        when(request.data()).thenReturn(data);
        when(commandRegistry.create(request)).thenReturn(command);
        when(command.execute(any(), any())).thenReturn(responseData);

        Response response = requestHandler.handle(request);

        assertEquals(TOKEN_MISSING, response.token());
        assertEquals(responseData, response.responseData());

        verify(commandRegistry).create(request);
        verify(command).execute(any(), any());
    }
}
