package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginCommand implements Command {
    private String username;
    private String password;

    @Override
    public String execute(User user) {
        if (!user.getPassword().equals(password)) {
            throw new PasswordNotCorrectException("The provided password is not correct!");
        }
        return "Successful login!";
    }

}
