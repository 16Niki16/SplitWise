package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateAccountCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.StatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.TransformCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CommandNotKnownException;
import bg.sofia.uni.fmi.mjt.splitwise.server.PasswordHasher;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;

import java.util.EnumMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.ADD_FRIEND;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.CREATE_ACCOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.CREATE_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.GET_STATUS;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.HELP;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.LOGIN;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.PAID;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.SPLIT;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.SPLIT_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.command.CommandType.SWITCH_CURRENCY;

public class CommandRegistry {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;
    private final PasswordHasher passwordHasher;
    private static final Map<CommandType, Command<?>> COMMANDS = new EnumMap<>(CommandType.class);

    public CommandRegistry(ApplicationServices applicationServices, SessionsManager sessionsManager, PasswordHasher passwordHasher) {
        this.applicationServices = applicationServices;
        this.sessionsManager = sessionsManager;
        this.passwordHasher = passwordHasher;
        registerCommands();
    }

    private void registerCommands() {
        COMMANDS.put(CREATE_ACCOUNT, new CreateAccountCommand(applicationServices, passwordHasher));
        COMMANDS.put(LOGIN, new LoginCommand(applicationServices, sessionsManager, passwordHasher));
        COMMANDS.put(HELP, new HelpCommand());
        COMMANDS.put(ADD_FRIEND, new AddFriendCommand(applicationServices, sessionsManager));
        COMMANDS.put(CREATE_GROUP, new CreateGroupCommand(applicationServices, sessionsManager));
        COMMANDS.put(GET_STATUS, new StatusCommand(applicationServices, sessionsManager));
        COMMANDS.put(SPLIT, new SplitCommand(applicationServices, sessionsManager));
        COMMANDS.put(SPLIT_GROUP, new SplitGroupCommand(applicationServices, sessionsManager));
        COMMANDS.put(PAID, new PaidCommand(applicationServices, sessionsManager));
        COMMANDS.put(SWITCH_CURRENCY, new TransformCurrencyCommand(applicationServices, sessionsManager));
    }

    public Command<?> create(Request request) {
        Command<?> command = COMMANDS.get(request.commandType());

        if (command == null) {
            throw new CommandNotKnownException("The provided command is not in the list!");
        }

        return command;
    }
}
