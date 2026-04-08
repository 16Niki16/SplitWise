package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.SplitPersonNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.SplitResponse;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitCommand implements Command {
    private String debtor;
    private String reason;
    private BigDecimal amountToSplit;
    private ApplicationServices applicationServices;

    @Override
    public Response execute(User user) {
        UserService userService = applicationServices.getUserService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        User debtorProfile = userService.getUserByUsername(debtor);
        BigDecimal amount = amountToSplit.divide(BigDecimal.valueOf(2));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(amount, user.getCurrency());
        debtsService.addDebt(debtor, user.getUsername(), amountInBaseCurrency);
        BigDecimal amountInPersonCurrency =
            currencyService.transformAmount(amount, user.getCurrency(), debtorProfile.getCurrency());
        Notification splitNotification =
            new SplitPersonNotification(user.getUsername(), amountInPersonCurrency, reason,
                debtorProfile.getCurrency());
        notificationsService.addNotification(debtor, splitNotification);

        return SplitResponse.of(debtor);
    }
}
