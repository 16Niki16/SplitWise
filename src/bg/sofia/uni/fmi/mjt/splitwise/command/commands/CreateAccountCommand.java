package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UsernameAlreadyUsedException;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor

public class CreateAccountCommand implements Command {
    private String username;
    private String password;
    private UserService userService;

    @Override
    public String execute(User user) {
        if (userService.getUserByUsername(username) != null) {
            throw new UsernameAlreadyUsedException("The provided username is already taken!");
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new PasswordNotCorrectException(
                    "Password must contain at least 6 symbols(1 small letter, 1 capital letter and 1 number)");
        }
        User newAccount = User.createNewAccount(username, password);
        userService.addUser(newAccount);
        userService.updateFile();

        return "Account successfully created!";
    }
}
