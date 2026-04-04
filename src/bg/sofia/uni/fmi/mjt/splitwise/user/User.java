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
import static java.lang.Math.abs;

public class User {
    private static final double START = 0.00;
    private static final int FOUR = 4;
    private static final int CURRENCY_FRIENDS_EXIST = 3;
    private static final int CURRENCY_FRIENDS_NOT_EXIST = 2;
    private static final int ZERO = 0;
    private final String username;
    private final String password;
    private final Map<String, Double> friendList;
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

    public String getUsername() {
        return username;
    }

    public String getCurrency() {
        return currency;
    }

    public String changeCurrency(Map<String, Double> mapWithCurrency) {
        double exchangeRate = 0;
        double wantedCurrency = 0;
        String temporaryCurrency = "";

        for (Map.Entry<String, Double> map : mapWithCurrency.entrySet()) {

            if (map.getKey().equalsIgnoreCase(this.currency)) {
                exchangeRate = map.getValue();

            } else {
                wantedCurrency = map.getValue();
                temporaryCurrency = map.getKey();
            }
        }
        this.currency = temporaryCurrency;

        for (Map.Entry<String, Double> map : friendList.entrySet()) {
            this.friendList.put(map.getKey(), (map.getValue() / exchangeRate) * wantedCurrency);
        }
        return toString();
    }

    public double amountToAdd(Map<String, Double> mapWithCurrency, double amountPersonCurrency,
                              boolean transformCurrent) {
        double exchangeRate = 0;
        double wantedCurrency = 0;
        for (Map.Entry<String, Double> map : mapWithCurrency.entrySet()) {
            if (map.getKey().equalsIgnoreCase(this.currency)) {
                wantedCurrency = map.getValue();
            } else {
                exchangeRate = map.getValue();
            }
        }
        return (transformCurrent) ? (amountPersonCurrency / exchangeRate) * wantedCurrency :
            (amountPersonCurrency / wantedCurrency) * exchangeRate;
    }

    public void addFriend(String friend) {
        if (friendList.containsKey(friend)) {
            throw new AlreadyFriendsException("They are friends already");
        }

        friendList.put(friend, START);
    }

    public void checkAlreadyFriends(String friend) {
        if (friendList.containsKey(friend)) {
            throw new AlreadyFriendsException("They are friends already");
        }
    }

    public String appendMoney(String friend, double amount) {
        if (this.friendList.containsKey(friend)) {
            double newAmount = this.friendList.get(friend) + amount / 2;
            this.friendList.put(friend, newAmount);
            return toString();
        }
        throw new PersonNotFriendException("You are not still friends");
    }

    public String getStatus() {
        StringBuilder build = new StringBuilder();
        for (Map.Entry<String, Double> map : this.friendList.entrySet()) {
            if (map.getValue() < ZERO) {
                build.append(String.format("%s owes you %.2f%s.\n", map.getKey(), abs(map.getValue()), currency));
            } else if (map.getValue() > ZERO) {
                build.append(String.format("You owe %s %.2f%s.\n", map.getKey(), map.getValue(), currency));
            }
        }
        return build.toString();
    }

    public String paidMoney(String friend, double amount) {
        if (this.friendList.containsKey(friend)) {
            double newAmount = this.friendList.get(friend) - amount;
            this.friendList.put(friend, newAmount);
            return toString();
        }
        throw new PersonNotFriendException("You are not still friends");
    }

    public void checkUserPasswordValid(String username, String password) {
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
