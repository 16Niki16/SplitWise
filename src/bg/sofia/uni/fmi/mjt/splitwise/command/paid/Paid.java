package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.GetExchangeRate;
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
import java.net.http.HttpClient;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Paid implements PaidAPI {
    private final ReaderWriterCreator notificationDirectory;
    private final ReaderWriterCreator directory;
    private final User user;
    private final ReaderWriterCreator exceptions;
    private final ReaderWriterCreator tempNotif;
    private final HttpClient httpClient;

    public Paid(ReaderWriterCreator directory, User user, ReaderWriterCreator notificationDirectory,
                ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif, HttpClient httpClient) {
        this.directory = directory;
        this.user = user;
        this.notificationDirectory = notificationDirectory;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
        this.httpClient = httpClient;
    }

    public String personPay(Command command) throws PersonNotFriendException, FriendNotRegisteredException {
        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);
            String appendUser = user.paidMoney(command.args()[USERNAME_OWE], -1 * amount);

            UserAPI friend = User.of(Helpers.findFriendLine(command, USERNAME_OWE, directory));
            if (!friend.getCurrency().equals(user.getCurrency())) {
                GetExchangeRate rate = new GetExchangeRate(httpClient);
                amount = friend.amountToAdd(rate.exchange(user.getCurrency(), friend.getCurrency()), amount);
            }

            String appendReceiver = friend.paidMoney(command.line(), amount);
            Helpers.addInformation(
                    Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE], appendUser, appendReceiver,
                            directory), directory);

            PersonPayNotificationsAPI notification = new PersonPayNotifications(notificationDirectory, tempNotif);
            notification.addNotificationFriendPayment(command);
            return "Successfully paid!";

        } catch (IOException e) {

            ExceptionFormater.exceptionAdd(command.line(), "paid IO exception", e.getStackTrace(), exceptions);
            throw new RuntimeException("could not pay, server problem!", e);

        } catch (NotCorrectQueryException | URISyntaxException | UnknownCurrencyException e) {
            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return "Unsuccessful payment!";
        }
    }
}
