package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestHandler {

    private final CommandRegistry commandRegistry;
    private final SessionsManager sessionsManager;
    private final UserService userService;

    public Response handle(Request request) {
        try {
            Command command = commandRegistry.create(request);
            AuthenticationResult authenticationResult = resolveUser(request);
            ResponseData responseData = command.execute(authenticationResult.user());

            return new Response(authenticationResult.token(), responseData);

        } catch (RuntimeException e) {
            return new Response(request.token(), ErrorResponse.of(e.getMessage()));
        }
    }

    private AuthenticationResult resolveUser(Request request) {
        if (request.data() instanceof LoginData loginData) {
            User user = userService.getUserByUsername(loginData.username());
            String token = sessionsManager.createSession(user);

            return new AuthenticationResult(token, user);
        } else if (request.data() instanceof CreateAccountData) {

            return new AuthenticationResult(null, null);
        }
        User user = sessionsManager.getUserSession(request.token());
        return new AuthenticationResult(request.token(), user);
    }
}
