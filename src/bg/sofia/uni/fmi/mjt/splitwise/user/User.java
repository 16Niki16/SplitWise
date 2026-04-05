package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;

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
