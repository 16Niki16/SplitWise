package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriendAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.AddFriend;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.create.CreateGroup;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.GroupSplitClass;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.SplitAPI;
import bg.sofia.uni.fmi.mjt.splitwise.command.split.Split;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.Status;
import bg.sofia.uni.fmi.mjt.splitwise.command.status.StatusAPI;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.ADD_FRIEND;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.CREATE_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GET_STATUS;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.HELP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT_GROUP;

public class CommandExecutor {
    private String groupsDirectory;
    private String directory;

    public CommandExecutor(String directory, String groupsDirectory) {

        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
    }

    public String execute(Command command) {
        return switch (command.args()[COMMAND_NAME]) {
            case ADD_FRIEND -> {
                AddFriendAPI friend = new AddFriend(directory);
                yield friend.addingFriend(command.line(), command.args());
            }
            case CREATE_GROUP -> {
                CreateGroupAPI group = new CreateGroup(directory, groupsDirectory);
                yield group.createGroup(command.line(), command.args());
            }
            case SPLIT -> {
                SplitAPI split = new Split(directory);
                yield split.moneyOwe(command);
            }
            case SPLIT_GROUP -> {
                GroupSplitAPI splitG = new GroupSplitClass(directory, groupsDirectory);
                yield splitG.groupsOwe(command);
            }
            case GET_STATUS -> {
                StatusAPI status = new Status(directory, groupsDirectory);
                yield status.getStatus(command);
            }
            case HELP -> helpCommand(command);
            default -> "Unknown command";
        };
    }

    private String helpCommand(Command command) {
        return "Login: Username and password \n Commands: \n" +
            "add-friend <username> \n" +
            "create-group <group_name> <username> <username> ... <username>\n" +
            "split <amount> <username> <reason_for_payment>\n" +
            "split-group <amount> <group_name> <reason_for_payment>\n" +
            "get-status\n" +
            "paid <amount> <username>";
    }
}
