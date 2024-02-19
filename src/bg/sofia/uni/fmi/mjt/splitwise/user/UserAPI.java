package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;

import java.util.Map;

public interface UserAPI {
    /**
     * get the username of the user
     */
    String getUsername();

    /**
     * get the current currency
     * */
    String getCurrency();

    /**
     * change the current currency
     * */
    String changeCurrency(Map<String, String> mapWithCurrency);

    /**
     * add friend method
     */
    String addFriend(String friend);

    /**
     * check if users are already friends
     *
     * @param friend the friend we want to add
     * @throws AlreadyFriendsException if they have each other in friends already
     */
    void checkAlreadyFriends(String friend) throws AlreadyFriendsException;

    /**
     * appends money to the file
     *
     * @param friend the friend that we want to add debt
     * @param amount the amount that we want to add
     * @throws PersonNotFriendException if they are not still friends
     */
    String appendMoney(String friend, double amount) throws PersonNotFriendException;

    /**
     * get current user status
     * */
    String getStatus();

    /**
     * paid money
     **/
    String paidMoney(String friend, double amount) throws PersonNotFriendException;

    /**
     * check username and password valid
     * */
    void checkUserPasswordValid(String name, String password) throws PasswordNotCorrectException;
}
