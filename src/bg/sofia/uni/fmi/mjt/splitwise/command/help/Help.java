package bg.sofia.uni.fmi.mjt.splitwise.command.help;

public class Help {
    public static String getHelp() {
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
