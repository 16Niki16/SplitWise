package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.containers.ClientContainer;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddFriendTest {
    private ReaderWriterCreator creator;
    private ReaderWriterCreator exc;
    private User user;
    private ClientContainer container;
    private AddFriendAPI addFriend;
    private String data;
    private String except;
    private Command command;

    @BeforeEach
    void setUp() {
        data = """
            niki | niki123 | pepi 10.00
            kolio | kolio123
            pepi | pepi123 | niki -10.00
                    """;
        except = "";
        creator = mock();
        exc = mock();
        command = CommandCreator.newCommand("niki add-friend pepi");
        user = User.of("niki | niki123 | pepi 10.00");
        addFriend = new AddFriend(creator, user, exc, container);
    }

    @Test
    void testAddFriendValid() {
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend(command), "Friend kolio is added.",
            "mistake in adding");
    }

    @Test
    void testAddFriendAlreadyFriends() {
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend(command), "They are friends already",
            "mistake in adding");
    }

    @Test
    void testAddFriendNotRegistered() {
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend(command), "This person is not registered yet.",
            "mistake in adding");
    }
}
