package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.PayData;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.PersonPayNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.PayResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaidCommandTest {
    private static final String MESSAGE = "You successfully accepted the payment from ";
    private static final String DUMMY = "dummy";
    private static final String TOKEN = "token";
    private static final String USERNAME = "user";
    private static final String PAYER = "payer";

    @Mock
    private ApplicationServices applicationServices;
    @Mock
    private SessionsManager sessionsManager;
    @Mock
    private UserService userService;
    @Mock
    private DebtsService debtsService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private NotificationsService notificationsService;

    @InjectMocks
    private PaidCommand paidCommand;

    @Test
    void testExecute_success() {
        User user = new User(USERNAME, DUMMY, null, null, "BGN");
        User payerUser = new User(PAYER, DUMMY, null, null, "EUR");

        PayData payData = new PayData(PAYER, new BigDecimal("100"));

        when(sessionsManager.getUserSession(TOKEN)).thenReturn(user);
        when(applicationServices.getUserService()).thenReturn(userService);
        when(applicationServices.getDebtsService()).thenReturn(debtsService);
        when(applicationServices.getCurrencyService()).thenReturn(currencyService);
        when(applicationServices.getNotificationsService()).thenReturn(notificationsService);

        when(userService.getUserByUsername(PAYER)).thenReturn(payerUser);

        when(currencyService.transformToBaseCurrency(payData.amount(), user.getCurrency()))
                .thenReturn(new BigDecimal("200"));

        when(currencyService.transformAmount(
                payData.amount(), user.getCurrency(), payerUser.getCurrency()))
                .thenReturn(new BigDecimal("50"));

        ResponseData response = paidCommand.execute(TOKEN, payData);
        PayResponse payResponse = (PayResponse) response;

        assertEquals(MESSAGE + PAYER, payResponse.getResponse());

        verify(debtsService).addDebt(USERNAME, PAYER, new BigDecimal("200"));
        verify(notificationsService).addNotification(eq(PAYER), any(PersonPayNotification.class));

    }
}
