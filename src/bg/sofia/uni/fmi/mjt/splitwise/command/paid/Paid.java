package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.NotificationAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.BufferedReader;
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
    private ReaderWriterCreator exceptions;
    private ReaderWriterCreator tempNotif;

    public Paid(ReaderWriterCreator directory, User user, ReaderWriterCreator notificationDirectory,
                ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif) {
        this.directory = directory;
        this.user = user;
        this.notificationDirectory = notificationDirectory;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
    }

    public String personPay(Command command) {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String readline;
            List<String> newLines = new ArrayList<>();
            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    NotificationAPI notif = new Notification(notificationDirectory, tempNotif);
                    notif.addNotificationFriendPayment(command);
                    newLines.add(user.paidMoney(command.args()[USERNAME_OWE].trim(),
                            -1 * Double.parseDouble(command.args()[AMOUNT])));
                } else if (splited[USER].trim().equals(command.args()[USERNAME_OWE].trim())) {
                    UserAPI friend = User.of(readline);
                    newLines.add(friend.paidMoney(command.line().trim(),
                            Double.parseDouble(command.args()[AMOUNT])));
                } else {
                    newLines.add(readline);
                }
            }
            Helpers.addInformation(newLines, directory);
            return "Successfully paid!";
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "paid IO exception", e.getStackTrace(), exceptions);
            throw new RuntimeException("could not pay, server problem!", e);
        } catch (PersonNotFriendException ee) {
            return ee.getLocalizedMessage();
        }
    }

}
