package bg.sofia.uni.fmi.mjt.splitwise.command;

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
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandRegistry {
    private final UserService userService;
    private final GroupService groupService;
    private final DebtsService debtsService;
    private static final Map<CommandType, CommandParser> COMMANDS = new EnumMap<>(CommandType.class);
    private static final Command HELP_COMMAND = new HelpCommand();

    public CommandRegistry(UserService userService, GroupService groupService, DebtsService debtsService) {
        this.userService = userService;
        this.groupService = groupService;
        this.debtsService = debtsService;
        registerCommands();
    }

    private void registerCommands() {
        COMMANDS.put(CommandType.CREATE_ACCOUNT, args -> new CreateAccountCommand(args[0], args[1], userService));
        COMMANDS.put(CommandType.LOGIN, args -> new LoginCommand(args[0], args[1]));
        COMMANDS.put(CommandType.HELP, args -> HELP_COMMAND);
        COMMANDS.put(CommandType.ADD_FRIEND, args -> new AddFriendCommand(userService, args[0]));
        COMMANDS.put(CommandType.CREATE_GROUP,
                args -> new CreateGroupCommand(args[0], getParticipants(args), groupService, userService));
        COMMANDS.put(CommandType.GET_STATUS, args -> new StatusCommand(debtsService));
        COMMANDS.put(CommandType.SPLIT,
                args -> new SplitCommand(args[1], args[2], new BigDecimal(args[0]), userService, debtsService));
        COMMANDS.put(CommandType.SPLIT_GROUP,
                args -> new GroupSplitCommand(args[1], new BigDecimal(args[0]), args[2], groupService, userService, debtsService));
        COMMANDS.put(CommandType.PAID, args -> new PaidCommand(debtsService, args[1], new BigDecimal(args[0])));
    }

    public static Command create(CommandLine commandLine) {
        CommandType commandType = CommandType.of(commandLine.line());
        CommandParser parser = COMMANDS.get(commandType);
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
