package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import java.util.HashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class User implements UserAPI {
    private static final double START = 0.00;
    private static final int ZERO = 0;
    private String username;
    private String password;
    private Map<String, Double> friendList;

    private User(String username, String password, Map<String, Double> friendList) {
        this.username = username;
        this.password = password;
        this.friendList = friendList;
    }

    public static User of(String line) {
        String[] splitLine = line.split("\\|");
        if (splitLine.length == THREE) {
            return new User(splitLine[USER], splitLine[PASSWORD], extractFriends(splitLine[FRIEND_LIST]));
        }
        return new User(splitLine[USER], splitLine[PASSWORD], new HashMap<>());
    }

    private static Map<String, Double> extractFriends(String friends) {
        String[] splitedFriends = friends.split(",");
        Map<String, Double> friendsOwes = new HashMap<>();
        for (String spl : splitedFriends) {
            String[] mapItem = spl.split(" ");
            friendsOwes.put(mapItem[USER], Double.valueOf(mapItem[AMOUNT]));
        }
        return friendsOwes;
    }

    public String getUsername() {
        return username;
    }

    public String addFriend(String friend) {
        friendList.put(friend, START);
        return toString();
    }

    public void checkAlreadyFriends(String friend) throws AlreadyFriendsException {
        if (friendList.containsKey(friend)) {
            throw new AlreadyFriendsException("They are friends already");
        }
    }

    public String appendMoney(String friend, double amount) throws PersonNotFriendException {
        if (this.friendList.containsKey(friend)) {
            double newAmount = this.friendList.get(friend) + amount / 2;
            this.friendList.put(friend, newAmount);
            return toString();
        }
        throw new PersonNotFriendException("You are not still friends");
    }

    public String paidMoney(String friend, double amount) throws PersonNotFriendException {
        if (this.friendList.containsKey(friend)) {
            double newAmount = this.friendList.get(friend) - amount;
            this.friendList.put(friend, newAmount);
            return toString();
        }
        throw new PersonNotFriendException("You are not still friends");
    }

    @Override
    public void checkUserPasswordValid(String username, String password) throws PasswordNotCorrectException {
        if (!username.equals(this.username) || !this.password.equals(password)) {
            throw new PasswordNotCorrectException("Password is not correct!");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s|%s|", username, password));

        for (Map.Entry<String, Double> entry : friendList.entrySet()) {
            result.append(String.format("%s %.2f,", entry.getKey(), entry.getValue()));
        }

        if (!result.isEmpty()) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
}
