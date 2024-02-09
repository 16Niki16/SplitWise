package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriendAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriend;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.help.Help;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.Paid;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplit;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.SplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.Split;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.Status;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.StatusAPI;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class CommandExecutor {
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator directory;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator exceptionsDirectory;
    private final ReaderWriterCreator tempNotif;

    public CommandExecutor(String directory, String groupsDirectory, String notificationsDirectory,
                           String exceptionsDirectory, String tempNotif) {

        this.directory = new ReaderWriterCreator(directory);
        this.groupsDirectory = new ReaderWriterCreator(groupsDirectory);
        this.notificationsDirectory = new ReaderWriterCreator(notificationsDirectory);
        this.exceptionsDirectory = new ReaderWriterCreator(exceptionsDirectory);
        this.tempNotif = new ReaderWriterCreator(tempNotif);
    }

    public String execute(Command command, User user) {
        try {
            return switch (CommandType.of(command.args()[COMMAND_NAME].strip())) {
                case CommandType.ADD_FRIEND, CommandType.CREATE_GROUP -> executeCreate(command, user);
                case CommandType.SPLIT, CommandType.SPLIT_GROUP -> executeSplit(command, user);
                case CommandType.PAID, CommandType.GROUP_PAID -> executePaid(command, user);
                case CommandType.GET_STATUS -> {
                    StatusAPI status = new Status(directory, groupsDirectory, exceptionsDirectory);
                    yield status.getStatus(command);
                }
                case CommandType.HELP -> Help.getHelp();
            };
        } catch (UnknownCommandException e) {
            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(),
                exceptionsDirectory);
            return "Unknown command";
        }
    }

    private String executePaid(Command command, User user) throws UnknownCommandException {
        return switch (CommandType.of(command.args()[COMMAND_NAME])) {
            case CommandType.PAID -> {
                if (command.args().length < THREE) {
                    yield "Not enough arguments";
                } else if (!command.args()[AMOUNT].matches("\\d+(\\.\\d+)?")) {
                    yield "Amount is not a number type or is a negative number";
                }
                PaidAPI paid = new Paid(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield paid.personPay(command);
            }
            case CommandType.GROUP_PAID -> {
                if (command.args().length < FOUR) {
                    yield "Not enough arguments";
                }
                PaidGroupAPI payment =
                    new PaidGroup(groupsDirectory, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield payment.personPaidToGroup(command);
            }
            default -> "Unknown command";
        };
    }

    private String executeCreate(Command command, User user) throws UnknownCommandException {
        return switch (CommandType.of(command.args()[COMMAND_NAME])) {
            case CommandType.ADD_FRIEND -> {
                if (command.args().length < TWO) {
                    yield "Not enough arguments";
                }
                AddFriendAPI friend = new AddFriend(directory, user, exceptionsDirectory);
                yield friend.addingFriend(command.line(), command.args());
            }
            case CommandType.CREATE_GROUP -> {
                CreateGroupAPI group = new CreateGroup(directory, groupsDirectory, exceptionsDirectory);
                yield group.createGroup(command.line(), command.args());
            }
            default -> "Unknown command";
        };
    }

    private String executeSplit(Command command, User user) throws UnknownCommandException {
        return switch (CommandType.of(command.args()[COMMAND_NAME])) {
            case CommandType.SPLIT -> {
                if (command.args().length < FOUR) {
                    yield "Not enough arguments";
                } else if (!command.args()[AMOUNT].matches("\\d+(\\.\\d+)?")) {
                    yield "Amount is not a number type or is a negative number";
                }
                SplitAPI split = new Split(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield split.moneyOwe(command);
            }
            case CommandType.SPLIT_GROUP -> {
                if (command.args().length < FOUR) {
                    yield "Not enough arguments";
                }
                GroupSplitAPI splitG =
                    new GroupSplit(groupsDirectory, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield splitG.groupsOwe(command);
            }
            default -> "Unknown command";
        };
    }
}
