package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitCommand implements Command {
    private String debtor;
    private String reason;
    private BigDecimal amountToSplit;
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();

        BigDecimal amount = amountToSplit.divide(BigDecimal.valueOf(2));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(amount, user.getCurrency());
        debtsService.addDebt(debtor, user.getUsername(), amountInBaseCurrency);
        debtsService.updateFile();
        return null;
    }
}
