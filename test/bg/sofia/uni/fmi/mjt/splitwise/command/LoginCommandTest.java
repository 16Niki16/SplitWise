package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.PersonPayNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.LoginResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseStatus;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginCommandTest {
    private static final String DUMMY = "dummy";
    @Mock
    private ApplicationServices applicationServices;
    @Mock
    private NotificationsService notificationsService;
    @InjectMocks
    private LoginCommand loginCommand;

    @Test
    void testCorrectCredentialsLogin() {
        User user = new User(DUMMY, DUMMY, null, null, DUMMY);
        LoginData data = new LoginData(DUMMY, DUMMY);
        Notification notification = new PersonPayNotification(DUMMY, BigDecimal.ZERO, DUMMY);
        List<Notification> notifications = List.of(notification);

        when(applicationServices.getNotificationsService()).thenReturn(notificationsService);

        when(notificationsService.getUserNotifications(user)).thenReturn(notifications);

        ResponseData response = loginCommand.execute(user, data);

        LoginResponse loginResponse = (LoginResponse) response;

        assertEquals(ResponseStatus.SUCCESSFUL, loginResponse.responseType());
        assertEquals(List.of(notification.getNotification()), loginResponse.notifications());
    }
}
