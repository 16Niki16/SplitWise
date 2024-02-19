package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;

import java.util.HashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class User implements UserAPI {
    private static final double START = 0.00;
    private static final int FOUR = 4;
    private static final int CURRENCY_FRIENDS_EXIST = 3;
    private static final int CURRENCY_FRIENDS_NOT_EXIST = 2;
    private static final int ZERO = 0;
    private String username;
    private String password;
    private Map<String, Double> friendList;
    private String currency;

    private User(String username, String password, Map<String, Double> friendList, String currency) {
        this.username = username;
        this.password = password;
        this.friendList = friendList;
        this.currency = currency;
    }

    public static User of(String line) {
        String[] splitLine = line.split("\\|");
        if (splitLine.length == FOUR) {
            return new User(splitLine[USER], splitLine[PASSWORD], extractFriends(splitLine[FRIEND_LIST]),
                    splitLine[CURRENCY_FRIENDS_EXIST]);
        }
        return new User(splitLine[USER], splitLine[PASSWORD], new HashMap<>(), splitLine[CURRENCY_FRIENDS_NOT_EXIST]);
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

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public String changeCurrency(Map<String, Double> mapWithCurrency) {
        changeCurrencyNumbers(mapWithCurrency);
        return toString();
    }

    private void changeCurrencyNumbers(Map<String, Double> mapWithCurrency) {
        double exchangeRate = 0;
        double wantedCurrency = 0;
        for (Map.Entry<String, Double> map : mapWithCurrency.entrySet()) {
            if (map.getKey().equalsIgnoreCase(this.currency)) {
                exchangeRate = map.getValue();
            } else {
                wantedCurrency = map.getValue();
                this.currency = map.getKey();
            }
        }
        for (Map.Entry<String, Double> map : friendList.entrySet()) {
            this.friendList.put(map.getKey(), (map.getValue() / exchangeRate) * wantedCurrency);
        }
    }

    @Override
    public String addFriend(String friend) {
        friendList.put(friend, START);
        return toString();
    }

    @Override
    public void checkAlreadyFriends(String friend) throws AlreadyFriendsException {
        if (friendList.containsKey(friend)) {
            throw new AlreadyFriendsException("They are friends already");
        }
    }

    @Override
    public String appendMoney(String friend, double amount) throws PersonNotFriendException {
        if (this.friendList.containsKey(friend)) {
            double newAmount = this.friendList.get(friend) + amount / 2;
            this.friendList.put(friend, newAmount);
            return toString();
        }
        throw new PersonNotFriendException("You are not still friends");
    }

    @Override
    public String getStatus() {
        StringBuilder build = new StringBuilder();
        for (Map.Entry<String, Double> map : this.friendList.entrySet()) {
            if (map.getValue() > ZERO) {
                build.append(String.format("%s owes you %.2f%s.\n", map.getKey(), map.getValue(), currency));
            } else if (map.getValue() < ZERO) {
                build.append(String.format("You owe %s %.2f%s.\n", map.getKey(), map.getValue(), currency));
            }
        }
        return build.toString();
    }

    @Override
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
        if (!this.password.equals(password)) {
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
            return result.substring(0, result.length() - 1) + "|" + currency;
        } else {
            result.append("|").append(currency);
        }

        return result.toString();
    }
}
