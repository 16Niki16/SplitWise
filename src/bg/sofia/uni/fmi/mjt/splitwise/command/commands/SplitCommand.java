package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.SplitData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.SplitPersonNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.SplitResponse;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitCommand implements Command<SplitData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, SplitData splitData) {
        User user = sessionsManager.getUserSession(token);
        UserService userService = applicationServices.getUserService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        User debtorProfile = userService.getUserByUsername(splitData.debtor());
        BigDecimal amount = splitData.amountToSplit().divide(BigDecimal.valueOf(2));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(amount, user.getCurrency());
        debtsService.addDebt(splitData.debtor(), user.getUsername(), amountInBaseCurrency);
        BigDecimal amountInPersonCurrency =
                currencyService.transformAmount(amount, user.getCurrency(), debtorProfile.getCurrency());
        Notification splitNotification =
                new SplitPersonNotification(user.getUsername(), amountInPersonCurrency, splitData.reason(),
                        debtorProfile.getCurrency());
        notificationsService.addNotification(splitData.debtor(), splitNotification);

        return SplitResponse.of(splitData.debtor());
    }
}
