package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateAccountCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.GroupSplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.StatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CommandNotKnownException;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;

import java.util.EnumMap;
import java.util.Map;

public class CommandRegistry {
    private final ApplicationServices applicationServices;
    private static final Map<CommandType, CommandParser> COMMANDS = new EnumMap<>(CommandType.class);
    private static final Command HELP_COMMAND = new HelpCommand();

    public CommandRegistry(ApplicationServices applicationServices) {
        this.applicationServices = applicationServices;
        registerCommands();
    }

    private void registerCommands() {
        COMMANDS.put(CommandType.CREATE_ACCOUNT, data -> new CreateAccountCommand(data, applicationServices));
        COMMANDS.put(CommandType.LOGIN, data -> new LoginCommand(data, applicationServices));
        COMMANDS.put(CommandType.HELP, data -> HELP_COMMAND);
        COMMANDS.put(CommandType.ADD_FRIEND, data -> new AddFriendCommand(data, applicationServices));
        COMMANDS.put(CommandType.CREATE_GROUP, data -> new CreateGroupCommand(data, applicationServices));
        COMMANDS.put(CommandType.GET_STATUS, data -> new StatusCommand(applicationServices));
        COMMANDS.put(CommandType.SPLIT, data -> new SplitCommand(data, applicationServices));
        COMMANDS.put(CommandType.SPLIT_GROUP, data -> new GroupSplitCommand(data, applicationServices));
        COMMANDS.put(CommandType.PAID, data -> new PaidCommand(data, applicationServices));
    }

    public Command create(Request request) {
        CommandParser parser = COMMANDS.get(request.commandType());
        if (parser == null) {
            throw new CommandNotKnownException("The provided command is not in the list!");
        }
        return parser.parse(request.data());
    }
}
