package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor

public class PaidCommand implements Command {
    private DebtsService debtsService;
    private String payer;
    private BigDecimal amount;

    @Override
    public String execute(User user) {
        debtsService.addDebt(user.getUsername(), payer, amount);
        return "successful payment";
    }
}
