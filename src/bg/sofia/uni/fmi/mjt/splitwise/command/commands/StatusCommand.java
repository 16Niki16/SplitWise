package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StatusCommand implements Command {
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        DebtsService debtsService = applicationServices.getDebtsService();
        List<Debt> userDebts = debtsService.getDebtsByUsername(user.getUsername());
        return userDebts.toString();
    }
}
