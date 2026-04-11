package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.PayData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.PersonPayNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.PayResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor

public class PaidCommand implements Command<PayData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;


    @Override
    public Response execute(String token, PayData payData) {
        User user = sessionsManager.getUserSession(token);
        UserService userService = applicationServices.getUserService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        User payerAccount = userService.getUserByUsername(payData.payer());
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(payData.amount(), user.getCurrency());
        debtsService.addDebt(user.getUsername(), payData.payer(), amountInBaseCurrency);

        BigDecimal amountInPersonCurrency =
            currencyService.transformAmount(payData.amount(), user.getCurrency(), payerAccount.getCurrency());
        Notification payNotification =
            new PersonPayNotification(user.getUsername(), amountInPersonCurrency, payerAccount.getCurrency());
        notificationsService.addNotification(payData.payer(), payNotification);
        return new Response(token, PayResponse.of(payData.payer()));
    }
}
