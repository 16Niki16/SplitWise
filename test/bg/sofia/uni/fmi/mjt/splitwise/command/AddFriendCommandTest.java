package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.AddFriendData;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.AddFriendResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseStatus;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddFriendCommandTest {
    private static final String DUMMY = "dummy";
    private static final String FRIEND = "friend";
    private static final String MESSAGE = "You successfully added ";
    @Mock
    private ApplicationServices applicationServices;
    @Mock
    private SessionsManager sessionsManager;
    @Mock
    private UserService userService;
    @InjectMocks
    private AddFriendCommand addFriendCommand;


    @Test
    void testAddFriend() {
        User user = new User(DUMMY, DUMMY, null, null, DUMMY);
        User friend = new User(FRIEND, DUMMY, null, null, DUMMY);
        AddFriendData data = new AddFriendData(FRIEND);

        when(applicationServices.getUserService()).thenReturn(userService);
        when(sessionsManager.getUserSession(any())).thenReturn(user);
        when(userService.getUserByUsername(FRIEND)).thenReturn(friend);

        ResponseData response = addFriendCommand.execute(DUMMY, data);
        AddFriendResponse addFriendResponse = (AddFriendResponse) response;

        assertEquals(ResponseStatus.SUCCESSFUL, addFriendResponse.statusResponse());
        assertEquals(addFriendResponse.getResponse(), MESSAGE + FRIEND);
    }
}
