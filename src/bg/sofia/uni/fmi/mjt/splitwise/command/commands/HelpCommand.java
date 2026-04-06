package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

public class HelpCommand implements Command {
    @Override
    public String execute(User user) {
        return """
            * Commands:
             - add-friend <username>
             - create-group <group_name> <username> <username> ... <username>
             - split <amount> <username> <reason_for_payment>
             - split-group <amount> <group_name> <reason_for_payment>
             - get-status
             - paid <amount> <username>
             - paid-group <amount> <user> <group_name>
             - switch-currency <currency>""";
    }
}
