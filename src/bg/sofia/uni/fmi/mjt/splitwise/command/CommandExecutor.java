package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.GroupSplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.SplitCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.TransformCurrency;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidCommand;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.PaidGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Status;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotNumberException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionHandler;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;

public class CommandExecutor {
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator directory;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public CommandExecutor(String directory, String groupsDirectory, String notificationsDirectory, String tempNotif) {

        this.directory = new ReaderWriterCreator(directory);
        this.groupsDirectory = new ReaderWriterCreator(groupsDirectory);
        this.notificationsDirectory = new ReaderWriterCreator(notificationsDirectory);
        this.tempNotif = new ReaderWriterCreator(tempNotif);
    }

    public String execute(CommandLine command, User user, ExchangeRate rate) {
        return switch (ExceptionHandler.checkCommandLength(CommandType.of(command.args()[COMMAND_NAME].strip()),
            command.args())) {

            case CommandType.ADD_FRIEND, CommandType.CREATE_GROUP -> executeCreate(command, user);

            case CommandType.SPLIT, CommandType.SPLIT_GROUP -> executeSplit(command, user, rate);

            case CommandType.PAID, CommandType.GROUP_PAID -> executePaid(command, user, rate);

            case CommandType.GET_STATUS -> {
                Status status = new Status(groupsDirectory, user, rate);
                yield status.getStatus(command);
            }

            case CommandType.HELP -> {
                HelpCommand help = new HelpCommand();
                yield help.execute();
            }
            case SWITCH_CURRENCY -> {
                TransformCurrency transform = new TransformCurrency(directory, user, rate);
                yield transform.changeCurrency(command);
            }
        };
    }

    private String executeCreate(CommandLine command, User user)
        throws UnknownCommandException {
        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.ADD_FRIEND -> {
                Command friend = new AddFriendCommand(directory, user);
                yield friend.execute(command.args());
            }

            case CommandType.CREATE_GROUP -> {
                Command group = new CreateGroupCommand(user, directory, groupsDirectory);
                yield group.execute(command.args());
            }

            default -> "Unknown command";
        };
    }

    private String executePaid(CommandLine command, User user, ExchangeRate rate)
        throws UnknownCommandException, NotNumberException, PersonNotFriendException, FriendNotRegisteredException {

        ExceptionHandler.checkNumber(command.args()[AMOUNT]);

        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.PAID -> {
                PaidCommand paid = new PaidCommand(directory, user, notificationsDirectory, tempNotif, rate);
                yield paid.personPay(command);
            }

            case CommandType.GROUP_PAID -> {
                PaidGroup payment =
                    new PaidGroup(groupsDirectory, notificationsDirectory, tempNotif, directory, user, rate);
                yield payment.personPaidToGroup(command);
            }

            default -> "Unknown command";
        };
    }

    private String executeSplit(CommandLine command, User user, ExchangeRate rate)
        throws UnknownCommandException, NotNumberException {

        ExceptionHandler.checkNumber(command.args()[AMOUNT]);

        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.SPLIT -> {
                SplitCommand split = new SplitCommand(directory, user, notificationsDirectory, tempNotif, rate);
                yield split.moneyOwe(command);
            }

            case CommandType.SPLIT_GROUP -> {
                Command splitG =
                    new GroupSplitCommand(groupsDirectory, notificationsDirectory, tempNotif, user, rate);
                yield splitG.execute(command.args());
            }

            default -> "Unknown command";
        };
    }
}
