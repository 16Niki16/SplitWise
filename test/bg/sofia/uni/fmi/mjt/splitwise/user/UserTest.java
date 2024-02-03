package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    private static final String userString = "niki | niki123 | kiro 0.00";
    private User user;

    @BeforeEach
    void setUp() {
        user = User.of(userString);
    }

    @Test
    void testToString() {
        assertEquals(user.toString(), "niki | niki123 | kiro 0.00", "mistake in creating user");
    }

    @Test
    void testAddFriend() {
        User userTest = User.of(userString);
        assertEquals(userTest.addFriend("kolio"), "niki | niki123 | kiro 0.00, kolio 0.00", "mistake in adding friend");
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
        assertEquals(userTest.appendMoney("kiro", 10), "niki | niki123 | kiro 5.00",
            "wrong operation in append money");
    }

    @Test
    void testPaidMoney() throws PersonNotFriendException {
        User userTest = User.of(userString);
        String str = userTest.appendMoney("kiro", 20);
        assertEquals(userTest.paidMoney("kiro", 5), "niki | niki123 | kiro 5.00",
            "wrong operation in paying money");
    }

}
