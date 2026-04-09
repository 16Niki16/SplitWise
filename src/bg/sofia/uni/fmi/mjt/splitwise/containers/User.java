package bg.sofia.uni.fmi.mjt.splitwise.containers;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class User {
    private static final String DEFAULT_CURRENCY = "EUR";

    private String username;
    private String password;
    @Setter
    private Set<String> friends;
    @Setter
    private Set<String> groups;
    private String currency;

    @JsonCreator
    public User(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("friends") Set<String> friends,
        @JsonProperty("groups") Set<String> groups,
        @JsonProperty("currency") String currency
    ) {
        this.username = username;
        this.password = password;
        this.friends = friends != null ? friends : new HashSet<>();
        this.groups = groups != null ? groups : new HashSet<>();
        this.currency = currency != null ? currency : DEFAULT_CURRENCY;
    }

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
