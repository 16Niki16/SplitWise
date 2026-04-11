package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestHandler {

    private final CommandRegistry commandRegistry;

    public Response handle(Request request) {
        try {
            Command command = commandRegistry.create(request);
            ResponseData responseData = command.execute(request.token(), request.data());
            String token = (responseData instanceof LoginResponse lr) ? lr.token() : request.token();

            return new Response(token, responseData);

        } catch (RuntimeException e) {
            return new Response(request.token(), ErrorResponse.of(e.getMessage()));
        }
    }
}
