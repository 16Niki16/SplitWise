import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.server.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Main {
    public static void main(String[] args) {
        String directory = "DataFiles\\UserData.txt";
        String groupsDirectory = "DataFiles\\GroupsFile.txt";
        String notificationsDirectory = "DataFiles\\Notifications.txt";
        /*try (
            Reader peopleReader = new FileReader(directory);
            Reader groupsReader = new FileReader(groupsDirectory);
            Reader notificationsReader = new FileReader(notificationsDirectory);

            Writer appendPeople = new FileWriter(directory, true);
            Writer notAppendPeople = new FileWriter(directory, false);

            Writer appendGroup = new FileWriter(groupsDirectory, true);
            Writer notAppendGroup = new FileWriter(groupsDirectory, false);

            Writer appendNotification = new FileWriter(notificationsDirectory, true);
            Writer notAppendNotification = new FileWriter(notificationsDirectory, false)
        ) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        CommandExecutor ex = new CommandExecutor(directory, groupsDirectory, notificationsDirectory);
        Server server = new Server(ex);
        server.serverStart();
    }
}