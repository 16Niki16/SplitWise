package bg.sofia.uni.fmi.mjt.splitwise.command.create;

import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddFriendTest {
    private ReaderWriterCreator creator;
    private User user;
    private AddFriendAPI addFriend;
    private String data;
    @BeforeEach
    void setUp() {
        data = """
            niki | niki123 | pepi 10.00
            kolio | kolio123
            pepi | pepi123 | niki -10.00
                    """;
        creator = mock();
        user = User.of("niki | niki123 | pepi 10.00");
        addFriend = new AddFriend(creator, user);
    }

    @Test
    void testAddFriendValid() {
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend("niki", "add-friend", "kolio"), "Friend kolio is added.",
            "mistake in adding");
    }

    @Test
    void testAddFriendAlreadyFriends() {
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend("niki", "add-friend", "pepi"), "They are friends already",
            "mistake in adding");
    }
    @Test
    void testAddFriendNotRegistered(){
        when(creator.getRead()).thenAnswer(x -> new StringReader(data));
        when(creator.getNotAppend()).thenAnswer(x -> new StringWriter());
        assertEquals(addFriend.addingFriend("niki", "add-friend", "unknown"), "This person is not registered yet.",
            "mistake in adding");
    }
}
