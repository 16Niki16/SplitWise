package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginCommandTest {
    @Mock
    private ApplicationServices applicationServices;
    @Mock
    private Data data;
    @InjectMocks
    private LoginCommand loginCommand;

    void testCorrectCredentialsLoging() {
        when(data instanceof LoginData).thenReturn(true);
        User user = new User("Niki", "Niki123", null, null, "EUR");
        //when()
    }
}
