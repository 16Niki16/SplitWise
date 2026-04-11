package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.EmptyData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.StatusResponse;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class StatusCommand implements Command<EmptyData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, EmptyData emptyData) {
        User user = sessionsManager.getUserSession(token);
        DebtsService debtsService = applicationServices.getDebtsService();
        List<Debt> userDebts = debtsService.getDebtsByUsername(user.getUsername());

        return StatusResponse.of(userDebts);
    }
}
