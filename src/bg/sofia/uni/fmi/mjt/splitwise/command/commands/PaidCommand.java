package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.PersonPayNotification;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor

public class PaidCommand implements Command {
    private String payer;
    private BigDecimal amount;
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        UserService userService = applicationServices.getUserService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        User payerAccount = userService.getUserByUsername(payer);
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(amount, user.getCurrency());
        debtsService.addDebt(user.getUsername(), payer, amountInBaseCurrency);

        BigDecimal amountInPersonCurrency =
            currencyService.transformAmount(amount, user.getCurrency(), payerAccount.getCurrency());
        Notification payNotification =
            new PersonPayNotification(user.getUsername(), amountInPersonCurrency, payerAccount.getCurrency());
        notificationsService.addNotification(payer, payNotification);
        return "Successful payment";
    }
}
