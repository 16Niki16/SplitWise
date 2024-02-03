package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.NotificationAPI;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private String notificationDirectory = "DataFiles\\Notifications.txt";
    private String directory;
    private User user;

    public Split(String directory, User user) {
        this.user = user;
        this.directory = directory;
    }

    @Override
    public String moneyOwe(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String readline;
            List<String> newLines = new ArrayList<>();

            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    NotificationAPI noti = new Notification(notificationDirectory);
                    noti.addNotificationFriendSplit(command);
                    newLines.add(user.appendMoney(command.args()[USERNAME_OWE].trim(),
                        -1 * Double.parseDouble(command.args()[AMOUNT])));
                } else if (splited[USER].trim().equals(command.args()[USERNAME_OWE].trim())) {
                    User friend = User.of(readline);
                    newLines.add(friend.appendMoney(command.line().trim(),
                        Double.parseDouble(command.args()[AMOUNT])));
                } else {
                    newLines.add(readline);
                }
            }

            appendNewInformation(newLines);
            return command.args()[REASON];

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PersonNotFriendException ee) {
            return ee.getLocalizedMessage();
        }
    }

    private void appendNewInformation(List<String> lines) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(directory, false))) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
