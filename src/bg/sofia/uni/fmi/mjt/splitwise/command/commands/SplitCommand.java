package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitCommand implements Command {
    private String debtor;
    private String reason;
    private BigDecimal amountToSplit;
    private UserService userService;
    private DebtsService debtsService;

    @Override
    public String execute(User user) {
        BigDecimal amount = amountToSplit.divide(BigDecimal.valueOf(2));
        debtsService.addDebt(debtor, user.getUsername(), amount);
        debtsService.updateFile();
        return null;
    }
}
