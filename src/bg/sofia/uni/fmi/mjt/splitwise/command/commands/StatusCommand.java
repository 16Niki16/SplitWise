package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StatusCommand implements Command {
    private DebtsService debtsService;

    @Override
    public String execute(User user) {
        List<Debt> userDebts = debtsService.getDebtsByUsername(user.getUsername());
        return userDebts.toString();
    }
}
