package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.EmptyData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.StatusResponse;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StatusCommand implements Command<EmptyData> {
    private ApplicationServices applicationServices;

    @Override
    public ResponseData execute(User user, EmptyData emptyData) {
        DebtsService debtsService = applicationServices.getDebtsService();
        List<Debt> userDebts = debtsService.getDebtsByUsername(user.getUsername());

        return StatusResponse.of(userDebts);
    }
}
