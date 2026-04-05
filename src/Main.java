import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.server.Server;

public class Main {
    public static void main(String[] args) {
        String directory = "DataFiles\\UserData.txt";
        String groupsDirectory = "DataFiles\\GroupsFile.txt";
        String notificationsDirectory = "DataFiles\\Notifications.txt";
        String exceptionsDirectory = "DataFiles\\Exceptions.txt";
        String temporaryNotifications = "DataFiles\\TemporaryNotifications.txt";
        CommandExecutor ex =
            new CommandExecutor(directory, groupsDirectory, notificationsDirectory,
                temporaryNotifications);
        Server server = new Server(ex, directory, temporaryNotifications, exceptionsDirectory);
        server.serverStart();
    }
}