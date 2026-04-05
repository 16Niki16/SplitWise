package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class User {
    private String username;
    private String password;
    private Set<String> friends;
    private Set<String> groups;
    private String currency;

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
