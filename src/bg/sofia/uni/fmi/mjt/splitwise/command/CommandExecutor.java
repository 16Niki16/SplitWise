package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriend;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriendAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.help.Help;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.Paid;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.paid.PaidGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplit;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.Split;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.SplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.Status;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.StatusAPI;
import bg.sofia.uni.fmi.mjt.splitwise.containers.ClientContainer;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughArgumentsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotNumberException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionHandler;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;

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

    public String execute(Command command, User user, ClientContainer container) {
        try {
            return switch (ExceptionHandler.checkCommandLength(CommandType.of(command.args()[COMMAND_NAME].strip()),
                command.args())) {

                case CommandType.ADD_FRIEND, CommandType.CREATE_GROUP -> executeCreate(command, user, container);

                case CommandType.SPLIT, CommandType.SPLIT_GROUP -> executeSplit(command, user, container);

                case CommandType.PAID, CommandType.GROUP_PAID -> executePaid(command, user, container);

                case CommandType.GET_STATUS -> {
                    StatusAPI status = new Status(directory, groupsDirectory, exceptionsDirectory);
                    yield status.getStatus(command);
                }

                case CommandType.HELP -> Help.getHelp();
            }
                ;
        } catch (NotNumberException | NotEnoughArgumentsException | UnknownCommandException e) {
            ExceptionFormater.exceptionAdd(
                command.line(), e.getLocalizedMessage(), e.getStackTrace(), exceptionsDirectory);
            return e.getLocalizedMessage();
        }
    }

    private String executeCreate(Command command, User user, ClientContainer container)
        throws UnknownCommandException {
        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.ADD_FRIEND -> {
                AddFriendAPI friend = new AddFriend(directory, user, exceptionsDirectory, container);
                yield friend.addingFriend(command);
            }

            case CommandType.CREATE_GROUP -> {
                CreateGroupAPI group = new CreateGroup(directory, groupsDirectory, exceptionsDirectory);
                yield group.createGroup(command);
            }

            default -> "Unknown command";
        };
    }

    private String executePaid(Command command, User user, ClientContainer container)
        throws UnknownCommandException, NotNumberException {

        ExceptionHandler.checkNumber(command.args()[AMOUNT]);

        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.PAID -> {
                PaidAPI paid = new Paid(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield paid.personPay(command);
            }

            case CommandType.GROUP_PAID -> {
                PaidGroupAPI payment =
                    new PaidGroup(groupsDirectory, notificationsDirectory, exceptionsDirectory, tempNotif, directory);
                yield payment.personPaidToGroup(command);
            }

            default -> "Unknown command";
        };
    }

    private String executeSplit(Command command, User user, ClientContainer container)
        throws UnknownCommandException, NotNumberException {

        ExceptionHandler.checkNumber(command.args()[AMOUNT]);

        return switch (CommandType.of(command.args()[COMMAND_NAME])) {

            case CommandType.SPLIT -> {
                SplitAPI split = new Split(directory, user, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield split.moneyOwe(command);
            }

            case CommandType.SPLIT_GROUP -> {
                GroupSplitAPI splitG =
                    new GroupSplit(groupsDirectory, notificationsDirectory, exceptionsDirectory, tempNotif);
                yield splitG.groupsOwe(command);
            }

            default -> "Unknown command";
        };
    }
}
