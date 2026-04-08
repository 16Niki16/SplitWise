package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.DataParser;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CommandNotKnownException;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandRegistry {
    private final ApplicationServices applicationServices;
    private static final Map<CommandType, DataParser> COMMANDS = new EnumMap<>(CommandType.class);
    private static final Command HELP_COMMAND = new HelpCommand();

    public CommandRegistry(ApplicationServices applicationServices) {
        this.applicationServices = applicationServices;
        registerCommands();
    }

    private void registerCommands() {
    }

    public static Command create(Request request) {
        CommandType commandType = CommandType.of(commandLine.line());
        DataParser parser = COMMANDS.get(commandType);
        if (parser == null) {
            throw new CommandNotKnownException("The provided command is not in the list!");
        }

        return parser.parse(commandLine.args());
    }

    private Set<String> getParticipants(String[] args) {
        return Arrays.stream(args)
                .skip(1)
                .collect(Collectors.toSet());
    }
}
