package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.BufferedReader;
import java.io.IOException;

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

    public String personPay(Command command) throws PersonNotFriendException, FriendNotRegisteredException {
        try {

            String appendUser = user.paidMoney(command.args()[USERNAME_OWE],
                -1 * Double.parseDouble(command.args()[AMOUNT]));

            UserAPI friend = User.of(friendLine(command));
            String appendReceiver = friend.paidMoney(command.line(),
                Double.parseDouble(command.args()[AMOUNT]));

            Helpers.addInformation(
                Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE], appendUser, appendReceiver,
                    directory), directory);

            PersonPayNotificationsAPI notification = new PersonPayNotifications(notificationDirectory, tempNotif);
            notification.addNotificationFriendPayment(command);
            return "Successfully paid!";

        } catch (IOException e) {

            ExceptionFormater.exceptionAdd(command.line(), "paid IO exception", e.getStackTrace(), exceptions);
            throw new RuntimeException("could not pay, server problem!", e);

        }
    }

    private String friendLine(Command command)
        throws IOException, FriendNotRegisteredException {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] splited = line.split("\\|");
                if (splited[USER].equals(command.args()[USERNAME_OWE])) {
                    return line;
                }
            }
        }
        throw new FriendNotRegisteredException("This person is still not registered!");
    }
}
