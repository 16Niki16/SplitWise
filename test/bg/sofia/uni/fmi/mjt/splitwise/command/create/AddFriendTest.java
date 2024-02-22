package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
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
    private AddFriendAPI addFriend;
    private String data;
    private String except;

    @BeforeEach
    void setUp() {
        data = """
            niki|niki123|pepi 10.00|BGN
            kolio|kolio123|BGN
            pepi|pepi123|niki -10.00|BGN""";
        except = "";
        ReaderWriterCreator creator = mock();
        ReaderWriterCreator exc = mock();
        User user = User.of("niki|niki123|pepi 10.00|BGN");
        addFriend = new AddFriend(creator, user, exc);

        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        when(exc.getRead()).thenAnswer(x -> new StringReader(except));
        when(exc.getAppend()).thenAnswer(x -> new StringWriter());
    }

    @Test
    void testAddFriendValid() {
        Command command = CommandCreator.newCommand("niki add-friend kolio");
        assertEquals(addFriend.addingFriend(command), "Friend kolio is added.",
            "mistake in adding");
    }

    @Test
    void testAddFriendAlreadyFriends() {
        Command command = CommandCreator.newCommand("niki add-friend pepi");
        assertEquals(addFriend.addingFriend(command), "They are friends already",
            "mistake in adding");
    }

    @Test
    void testAddFriendNotRegistered() {
        Command command = CommandCreator.newCommand("niki add-friend unknown");
        assertEquals(addFriend.addingFriend(command), "This person is still not registered!",
            "mistake in adding");
    }

    @Test
    void testAddYourselfException(){
        Command command = CommandCreator.newCommand("niki add-friend niki");
        assertEquals(addFriend.addingFriend(command),"You can not add yourself as a friend!",
                "mistake in test add yourself");
    }
}
