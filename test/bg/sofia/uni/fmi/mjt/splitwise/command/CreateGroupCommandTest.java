    package bg.sofia.uni.fmi.mjt.splitwise.command;

    import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateGroupData;
    import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateGroupCommand;
    import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
    import bg.sofia.uni.fmi.mjt.splitwise.response.CreateGroupResponse;
    import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
    import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseStatus;
    import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
    import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
    import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
    import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.util.Set;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    public class CreateGroupCommandTest {
        private static final String DUMMY = "dummy";
        private static final String PARTICIPANT = "participant";
        private static final String PARTICIPANT_2 = "participant2";
        private static final String MESSAGE = "You created group called ";
        @Mock
        private ApplicationServices applicationServices;
        @Mock
        private SessionsManager sessionsManager;
        @InjectMocks
        private CreateGroupCommand createGroupCommand;


        @Test
        void testCreateGroup() {
            UserService userService = mock();
            GroupService groupService = mock();
            User user = new User(DUMMY, DUMMY, null, null, DUMMY);
            User participant1 = new User(PARTICIPANT, DUMMY, null, null, DUMMY);
            User participant2 = new User(PARTICIPANT_2, DUMMY, null, null, DUMMY);
            CreateGroupData data = new CreateGroupData(DUMMY, Set.of(PARTICIPANT, PARTICIPANT_2));

            when(applicationServices.getUserService()).thenReturn(userService);
            when(applicationServices.getGroupService()).thenReturn(groupService);
            when(sessionsManager.getUserSession(any())).thenReturn(user);
            when(userService.getUserByUsername(PARTICIPANT)).thenReturn(participant1);
            when(userService.getUserByUsername(PARTICIPANT_2)).thenReturn(participant2);

            ResponseData response = createGroupCommand.execute(DUMMY, data);
            CreateGroupResponse createGroupResponse = (CreateGroupResponse) response;

            assertEquals(ResponseStatus.SUCCESSFUL, createGroupResponse.responseStatus());
            assertEquals(createGroupResponse.getResponse(), MESSAGE + DUMMY);
        }
    }
