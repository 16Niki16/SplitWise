package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

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
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Paid implements PaidAPI {
    private ReaderWriterCreator notificationDirectory;
    private ReaderWriterCreator directory;
    private User user;

    public Paid(ReaderWriterCreator directory, User user, ReaderWriterCreator notificationDirectory) {
        this.directory = directory;
        this.user = user;
        this.notificationDirectory = notificationDirectory;
    }

    public String personPay(Command command) {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String readline;
            List<String> newLines = new ArrayList<>();

            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    NotificationAPI notif = new Notification(notificationDirectory);
                    notif.addNotificationFriendPayment(command);
                    newLines.add(user.paidMoney(command.args()[USERNAME_OWE].trim(),
                        -1 * Double.parseDouble(command.args()[AMOUNT])));
                } else if (splited[USER].trim().equals(command.args()[USERNAME_OWE].trim())) {
                    User friend = User.of(readline);
                    newLines.add(friend.paidMoney(command.line().trim(),
                        Double.parseDouble(command.args()[AMOUNT])));
                } else {
                    newLines.add(readline);
                }
            }
            appendNewInformation(newLines);
            return "Successfully paid!";
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PersonNotFriendException ee) {
            return ee.getLocalizedMessage();
        } catch (NumberFormatException e) {
            return "Number format exception";
        }
    }

    private void appendNewInformation(List<String> lines) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(directory.getNotAppend())) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        }
    }
}
