package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateAccountCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.StatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.TransformCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CommandNotKnownException;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;

import java.util.EnumMap;
import java.util.Map;

public class CommandRegistry {
    private final ApplicationServices applicationServices;
    private static final Map<CommandType, Command> COMMANDS = new EnumMap<>(CommandType.class);

    public CommandRegistry(ApplicationServices applicationServices) {
        this.applicationServices = applicationServices;
        registerCommands();
    }

    private void registerCommands() {
        COMMANDS.put(CommandType.CREATE_ACCOUNT, new CreateAccountCommand(applicationServices));
        COMMANDS.put(CommandType.LOGIN, new LoginCommand(applicationServices));
        COMMANDS.put(CommandType.HELP, new HelpCommand());
        COMMANDS.put(CommandType.ADD_FRIEND, new AddFriendCommand(applicationServices));
        COMMANDS.put(CommandType.CREATE_GROUP, new CreateGroupCommand(applicationServices));
        COMMANDS.put(CommandType.GET_STATUS, new StatusCommand(applicationServices));
        COMMANDS.put(CommandType.SPLIT, new SplitCommand(applicationServices));
        COMMANDS.put(CommandType.SPLIT_GROUP, new SplitGroupCommand(applicationServices));
        COMMANDS.put(CommandType.PAID, new PaidCommand(applicationServices));
        COMMANDS.put(CommandType.SWITCH_CURRENCY, new TransformCurrencyCommand(applicationServices));
    }

    public Command create(Request request) {
        Command command = COMMANDS.get(request.commandType());

        if (command == null) {
            throw new CommandNotKnownException("The provided command is not in the list!");
        }

        return command;
    }
}
