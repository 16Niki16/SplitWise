package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UsernameAlreadyUsedException;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateAccountResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor

public class CreateAccountCommand implements Command<CreateAccountData> {
    private ApplicationServices applicationServices;
    private SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, CreateAccountData createAccountData) {
        UserService userService = getUserService(createAccountData);
        User newAccount = User.createNewAccount(createAccountData.username(), createAccountData.password());
        userService.addUser(newAccount);

        return CreateAccountResponse.of(createAccountData.username());
    }

    private UserService getUserService(CreateAccountData createAccountData) {
        UserService userService = applicationServices.getUserService();

        if (userService.checkUserExists(createAccountData.username())) {
            throw new UsernameAlreadyUsedException("The provided username is already taken!");
        } else if (!createAccountData.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new PasswordNotCorrectException(
                "Password must contain at least 6 symbols(1 small letter, 1 capital letter and 1 number)");
        }
        return userService;
    }
}
