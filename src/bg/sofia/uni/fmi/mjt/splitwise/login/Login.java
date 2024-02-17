package bg.sofia.uni.fmi.mjt.splitwise.login;

import bg.sofia.uni.fmi.mjt.splitwise.containers.ClientContainer;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;

public class Login {
    public static User loginInSystem(String username, String password, ReaderWriterCreator directory,
                                     ClientContainer container)
        throws PasswordNotCorrectException, IOException {
        User user;
        try {
            if ((user = container.getUser(username)) != null) {
                user.checkUserPasswordValid(username, password);
                return user;
            }
            user = Helpers.checkInFileExtract(username, directory);
            user.checkUserPasswordValid(username, password);
            container.addUser(user);
            return user;
        } catch (FriendNotRegisteredException e) {
            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
                throw new PasswordNotCorrectException(
                    "Password must contain at least 6 symbols(1 small letter, 1 capital letter and 1 number)");
            }
            Helpers.appendToFile(username + "|" + password + "|BGN", directory);
            return User.of(username + "|" + password + "|BGN");
        }
    }
}
