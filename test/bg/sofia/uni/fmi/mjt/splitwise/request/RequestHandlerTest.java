package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHandlerTest {
    private RequestHandler requestHandler;
    private Request request;

    @BeforeEach
    void setUp() {
        CommandRegistry commandRegistry = mock();
        SessionsManager sessionsManager = mock();
        UserService userService = mock();
        requestHandler = new RequestHandler(commandRegistry, sessionsManager, userService);
    }

}
