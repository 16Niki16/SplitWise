package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    private static final String userString = "niki|niki123|kiro 0.00|BGN";
    private User user;

    @BeforeEach
    void setUp() {
        user = User.of(userString);
    }

    @Test
    void testToString() {
        assertEquals(user.toString(), "niki|niki123|kiro 0.00|BGN", "mistake in creating user");
    }

    @Test
    void testAddFriend() {
        User userTest = User.of(userString);
        assertEquals(userTest.addFriend("kolio"), "niki|niki123|kiro 0.00,kolio 0.00|BGN", "mistake in adding friend");
    }

    @Test
    void testGetUsername() {
        assertEquals(user.getUsername(), "niki", "mistake in extracting user");
    }

    @Test
    void testCheckAlreadyFriends() {
        assertThrows(AlreadyFriendsException.class, () -> user.checkAlreadyFriends("kiro"),
            "wrong checking for existence");
    }

    @Test
    void testAppendMoney() throws PersonNotFriendException {
        User userTest = User.of(userString);
        assertEquals(userTest.appendMoney("kiro", 10), "niki|niki123|kiro 5.00|BGN",
            "wrong operation in append money");
    }

    @Test
    void testPaidMoney() throws PersonNotFriendException{
        User userTest = User.of(userString);
        String str = userTest.appendMoney("kiro", 20);
        assertEquals(userTest.paidMoney("kiro", 5), "niki|niki123|kiro 5.00|BGN",
            "wrong operation in paying money");
    }

}
