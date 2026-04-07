package bg.sofia.uni.fmi.mjt.splitwise.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApplicationServices implements Service {
    private UserService userService;
    private GroupService groupService;
    private DebtsService debtsService;
    private CurrencyService currencyService;
    private NotificationsService notificationsService;
    private ExceptionsService exceptionsService;
}
