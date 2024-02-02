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
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.ADD_FRIEND;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.CREATE_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GET_STATUS;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_PAID;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.HELP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PAID;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT_GROUP;

public class CommandExecutor {
    private String groupsDirectory;
    private String directory;

    public CommandExecutor(String directory, String groupsDirectory) {

        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
    }

    public String execute(Command command, User user) {
        return switch (command.args()[COMMAND_NAME]) {
            case ADD_FRIEND -> {
                AddFriendAPI friend = new AddFriend(directory, user);
                yield friend.addingFriend(command.line(), command.args());
            }
            case CREATE_GROUP -> {
                CreateGroupAPI group = new CreateGroup(directory, groupsDirectory);
                yield group.createGroup(command.line(), command.args());
            }
            case SPLIT -> {
                SplitAPI split = new Split(directory, user);
                yield split.moneyOwe(command);
            }
            case SPLIT_GROUP -> {
                GroupSplitAPI splitG = new GroupSplit(groupsDirectory);
                yield splitG.groupsOwe(command);
            }
            case GET_STATUS -> {
                StatusAPI status = new Status(directory, groupsDirectory);
                yield status.getStatus(command);
            }
            case PAID -> {
                PaidAPI paid = new Paid(directory, user);
                yield paid.personPay(command);
            }
            case GROUP_PAID -> {
                PaidGroupAPI payment = new PaidGroup(groupsDirectory);
                yield payment.personPaidToGroup(command);
            }
            case HELP -> helpCommand(command);
            default -> "Unknown command";
        };
    }

    private String helpCommand(Command command) {
        return """
            Login: Username and password
            * Commands:
             - add-friend <username>
             - create-group <group_name> <username> <username> ... <username>
             - split <amount> <username> <reason_for_payment>
             - split-group <amount> <group_name> <reason_for_payment>
             - get-status +
             - paid <amount> <username>
             - group-paid <amount> <user> <group_name>
             """;
    }
}
