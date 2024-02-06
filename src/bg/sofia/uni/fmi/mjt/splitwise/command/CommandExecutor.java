package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriendAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriend;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
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
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.ADD_FRIEND;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.CREATE_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GET_STATUS;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_PAID;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.HELP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PAID;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT_GROUP;
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
        return switch (command.args()[COMMAND_NAME]) {
            case ADD_FRIEND, CREATE_GROUP -> executeCreate(command, user);
            case SPLIT, SPLIT_GROUP -> executeSplit(command, user);
            case PAID, GROUP_PAID -> executePaid(command, user);
            case GET_STATUS -> {
                StatusAPI status = new Status(directory, groupsDirectory, exceptionsDirectory);
                yield status.getStatus(command);
            }
            case HELP -> helpCommand(command);
            default -> "Unknown command";
        };
    }

    private String executePaid(Command command, User user) {
        return switch (command.args()[COMMAND_NAME]) {
            case PAID -> {
                if (command.args().length < THREE) {
                    yield "Not enough arguments";
                }
                PaidAPI paid = new Paid(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield paid.personPay(command);
            }
            case GROUP_PAID -> {
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

    private String executeCreate(Command command, User user) {
        return switch (command.args()[COMMAND_NAME]) {
            case ADD_FRIEND -> {
                if (command.args().length < TWO) {
                    yield "Not enough arguments";
                }
                AddFriendAPI friend = new AddFriend(directory, user, exceptionsDirectory);
                yield friend.addingFriend(command.line(), command.args());
            }
            case CREATE_GROUP -> {
                CreateGroupAPI group = new CreateGroup(directory, groupsDirectory, exceptionsDirectory);
                yield group.createGroup(command.line(), command.args());
            }
            default -> "Unknown command";
        };
    }

    private String executeSplit(Command command, User user) {
        return switch (command.args()[COMMAND_NAME]) {
            case SPLIT -> {
                if (command.args().length < FOUR) {
                    yield "Not enough arguments";
                } else if (!command.args()[AMOUNT].matches("-?\\d+(\\.\\d+)?")) {
                    yield "Amount is not a number type";
                }
                SplitAPI split = new Split(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield split.moneyOwe(command);
            }
            case SPLIT_GROUP -> {
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

    private String helpCommand(Command command) {
        return """
            * Commands:
             - add-friend <username>
             - create-group <group_name> <username> <username> ... <username>
             - split <amount> <username> <reason_for_payment>
             - split-group <amount> <group_name> <reason_for_payment>
             - get-status
             - paid <amount> <username>
             - group-paid <amount> <user> <group_name>
             """;
    }
}
