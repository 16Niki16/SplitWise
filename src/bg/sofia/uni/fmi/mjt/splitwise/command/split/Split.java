package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.NotificationAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private ReaderWriterCreator friends;
    private ReaderWriterCreator notifications;
    private User user;

    public Split(ReaderWriterCreator friends, User user, ReaderWriterCreator notifications) {
        this.user = user;
        this.friends = friends;
        this.notifications = notifications;
    }

    @Override
    public String moneyOwe(Command command) {
        try (BufferedReader r = new BufferedReader(friends.getRead())) {
            String readline;
            List<String> newLines = new ArrayList<>();

            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    NotificationAPI noti = new Notification(notifications);
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
            return "Successfully split the money!";

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PersonNotFriendException ee) {
            return ee.getLocalizedMessage();
        }
    }

    private void appendNewInformation(List<String> lines) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(friends.getNotAppend())) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        }
    }
}
