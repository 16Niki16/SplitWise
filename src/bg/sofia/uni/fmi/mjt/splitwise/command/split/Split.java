package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.NotificationAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private ReaderWriterCreator friends;
    private ReaderWriterCreator notifications;
    private User user;
    private ReaderWriterCreator exception;
    private ReaderWriterCreator tempNotif;

    public Split(ReaderWriterCreator friends, User user, ReaderWriterCreator notifications,
                 ReaderWriterCreator exception, ReaderWriterCreator tempNotif) {
        this.user = user;
        this.friends = friends;
        this.notifications = notifications;
        this.exception = exception;
        this.tempNotif = tempNotif;
    }

    @Override
    public String moneyOwe(Command command) {
        try (BufferedReader r = new BufferedReader(friends.getRead())) {
            String readline;
            List<String> newLines = new ArrayList<>();
            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    NotificationAPI noti = new Notification(notifications, tempNotif);
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
            Helpers.addInformation(newLines, friends);
            return "Successfully split the money!";
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Could not split the money IO.", e.getStackTrace(),
                exception);
            throw new RuntimeException("Could not split the money IO.", e);
        } catch (PersonNotFriendException ee) {
            ExceptionFormater.exceptionAdd(command.line(), ee.getLocalizedMessage(), ee.getStackTrace(), exception);
            return ee.getLocalizedMessage();
        }
    }

}
