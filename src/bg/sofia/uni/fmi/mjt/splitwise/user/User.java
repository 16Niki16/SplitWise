package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class User {
    private static final String DEFAULT_CURRENCY = "EUR";
    private String username;
    private String password;
    private Set<String> friends;
    private Set<String> groups;
    private String currency;

    public static User createNewAccount(String username, String password) {
        return new User(username, password, new HashSet<>(), new HashSet<>(), DEFAULT_CURRENCY);
    }

    public void changeCurrency(String newCurrency) {
        this.currency = newCurrency;
    }

    public void addFriend(String username) {
        if (friends.contains(username)) {
            throw new AlreadyFriendsException("You are already friends!");
        }

        friends.add(username);
    }

    public void addGroup(String groupID) {
        this.groups.add(groupID);
    }
}
