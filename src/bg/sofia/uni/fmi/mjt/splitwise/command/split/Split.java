package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.BufferedReader;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private ReaderWriterCreator directory;
    private ReaderWriterCreator notifications;
    private User user;
    private ReaderWriterCreator exception;
    private ReaderWriterCreator tempNotif;

    public Split(ReaderWriterCreator directory, User user, ReaderWriterCreator notifications,
                 ReaderWriterCreator exception, ReaderWriterCreator tempNotif) {
        this.user = user;
        this.directory = directory;
        this.notifications = notifications;
        this.exception = exception;
        this.tempNotif = tempNotif;
    }

    @Override
    public String moneyOwe(Command command) {
        try {

            String appendUser = user.appendMoney(command.args()[USERNAME_OWE],
                -1 * Double.parseDouble(command.args()[AMOUNT]));

            UserAPI friend = User.of(Helpers.findFriendLine(command, USERNAME_OWE, directory));
            String appendReceiver = friend.appendMoney(command.line(), Double.parseDouble(command.args()[AMOUNT]));

            Helpers.addInformation(
                Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE], appendUser, appendReceiver,
                    directory), directory);

            SplitPersonNotificationsAPI notification = new SplitPersonNotifications(notifications, tempNotif);
            notification.addNotificationFriendSplit(command);

            return "Successfully split the money!";
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Could not split the money IO.", e.getStackTrace(),
                exception);
            throw new RuntimeException("Could not split the money IO.", e);
        } catch (PersonNotFriendException | FriendNotRegisteredException ee) {
            ExceptionFormater.exceptionAdd(command.line(), ee.getLocalizedMessage(), ee.getStackTrace(), exception);
            return ee.getLocalizedMessage();
        }
    }

}
