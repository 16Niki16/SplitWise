package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UsernameAlreadyUsedException;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateAccountResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor

public class CreateAccountCommand implements Command {
    private CreateAccountData createAccountData;
    private ApplicationServices applicationServices;

    @Override
    public Response execute(User user) {
        UserService userService = applicationServices.getUserService();

        if (userService.getUserByUsername(createAccountData.username()) != null) {
            throw new UsernameAlreadyUsedException("The provided username is already taken!");
        } else if (!createAccountData.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new PasswordNotCorrectException(
                "Password must contain at least 6 symbols(1 small letter, 1 capital letter and 1 number)");
        }
        User newAccount = User.createNewAccount(createAccountData.username(), createAccountData.password());
        userService.addUser(newAccount);
        userService.updateFile();

        return CreateAccountResponse.of(createAccountData.username());
    }
}
