package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransformCurrencyCommand implements Command {
    private String newCurrency;
    private CurrencyService currencyService;

    @Override
    public String execute(User user) {
        currencyService.getRates()
                .thenAccept(rates -> {
                    if (!rates.containsKey(newCurrency)) {
                        throw new UnknownCurrencyException("Unknown currency: " + newCurrency);
                    }
                });
        user.changeCurrency(newCurrency);

        return "Successful currency change!";
    }
}
