package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor

public class PaidCommand implements Command {
    private String payer;
    private BigDecimal amount;
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(amount, user.getCurrency());
        debtsService.addDebt(user.getUsername(), payer, amountInBaseCurrency);
        return "successful payment";
    }
}
