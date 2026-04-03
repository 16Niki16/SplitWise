package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.SplitPersonNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.IOException;
import java.net.URISyntaxException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private final ReaderWriterCreator directory;
    private final ReaderWriterCreator notifications;
    private final User user;
    private final ReaderWriterCreator tempNotif;
    private final ExchangeRate rate;

    public Split(ReaderWriterCreator directory, User user, ReaderWriterCreator notifications,
                 ReaderWriterCreator tempNotif, ExchangeRate rate) {
        this.user = user;
        this.directory = directory;
        this.notifications = notifications;
        this.tempNotif = tempNotif;
        this.rate = rate;
    }

    @Override
    public String moneyOwe(CommandLine command) {
        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);
            String appendUser = user.appendMoney(command.args()[USERNAME_OWE], -1 * amount);
            UserAPI friend = User.of(Helpers.findFriendLine(command, USERNAME_OWE, directory));

            if (!friend.getCurrency().equals(user.getCurrency())) {
                amount = friend.amountToAdd(rate.exchange(user.getCurrency(), friend.getCurrency()), amount, true);
            }
            String appendReceiver = friend.appendMoney(command.line(), amount);

            Helpers.addInformation(
                Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE], appendUser, appendReceiver,
                    directory), directory);

            SplitPersonNotificationsAPI notification = new SplitPersonNotifications(notifications, tempNotif);
            notification.addNotificationFriendSplit(command);

            return "Successfully split the money!";
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Could not split the money IO.", e);
        }
    }

}
