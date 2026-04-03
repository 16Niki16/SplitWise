package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.IOException;
import java.net.URISyntaxException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Paid implements PaidAPI {
    private final ReaderWriterCreator notificationDirectory;
    private final ReaderWriterCreator directory;
    private final User user;
    private final ReaderWriterCreator tempNotif;
    private final ExchangeRate rate;

    public Paid(ReaderWriterCreator directory, User user, ReaderWriterCreator notificationDirectory,
                ReaderWriterCreator tempNotif, ExchangeRate rate) {
        this.directory = directory;
        this.user = user;
        this.notificationDirectory = notificationDirectory;
        this.tempNotif = tempNotif;
        this.rate = rate;
    }

    @Override
    public String personPay(CommandLine command) {
        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);
            UserAPI friend = User.of(Helpers.findFriendLine(command, USERNAME_OWE, directory));
            String appendUser = user.paidMoney(command.args()[USERNAME_OWE], -1 * amount);

            if (!friend.getCurrency().equals(user.getCurrency())) {
                amount = friend.amountToAdd(rate.exchange(user.getCurrency(), friend.getCurrency()), amount, true);
            }

            String appendReceiver = friend.paidMoney(command.line(), amount);
            Helpers.addInformation(
                Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE], appendUser, appendReceiver,
                    directory), directory);

            PersonPayNotificationsAPI notification = new PersonPayNotifications(notificationDirectory, tempNotif);
            notification.addNotificationFriendPayment(command);
            return "Successfully paid!";

        } catch (IOException e) {
            throw new RuntimeException("could not pay, server problem!", e);

        } catch (URISyntaxException e) {
            throw new RuntimeException("future fix", e);
        }
    }
}
