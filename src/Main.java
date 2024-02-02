import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.server.Server;

public class Main {
    public static void main(String[] args) {
        String directory = "DataFiles\\UserData.txt";
        String groupsDirectory = "DataFiles\\GroupsFile.txt";
        CommandExecutor ex = new CommandExecutor(directory, groupsDirectory);
        Server server = new Server(ex);
        server.serverStart();
    }
}