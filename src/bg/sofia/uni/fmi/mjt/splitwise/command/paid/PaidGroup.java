package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.GetExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.group.GroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;

public class PaidGroup implements PaidGroupAPI {
    private static final int GROUP_INDEX = 3;
    private static final int AMOUNT = 1;
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator notifications;
    private final ReaderWriterCreator exceptions;
    private final ReaderWriterCreator tempNotif;
    private final ReaderWriterCreator friends;
    private final User user;
    private final HttpClient client;

    public PaidGroup(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications,
                     ReaderWriterCreator exceptions, ReaderWriterCreator tempNotif, ReaderWriterCreator friends,
                     User user, HttpClient client) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
        this.exceptions = exceptions;
        this.tempNotif = tempNotif;
        this.friends = friends;
        this.user = user;
        this.client = client;
    }

    @Override
    public String personPaidToGroup(Command command) {
        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);
            GroupAPI updateGroup = Group.ofSplit(Helpers.findGroupLine(command, groupsDirectory, GROUP_INDEX));
            if (!user.getCurrency().equalsIgnoreCase("bgn")) {
                GetExchangeRate rate = new GetExchangeRate(client);
                amount = user.amountToAdd(rate.exchange(user.getCurrency(), "bgn"), amount, false);
            }
            String payment = updateGroup.payInGroup(command, notifications, tempNotif, friends, user, amount);

            Helpers.addInformation(Helpers.updatedGroup(command.args()[GROUP_INDEX], groupsDirectory, payment),
                    groupsDirectory);
            return "Successful payment in a group!";

        } catch (GroupDoesNotExistException | NoMembersToPayException | PersonNotFriendException |
                 FriendNotRegisteredException | NotCorrectQueryException | UnknownCurrencyException e) {

            ExceptionFormater.exceptionAdd(command.line(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();

        } catch (IOException | URISyntaxException e) {

            ExceptionFormater.exceptionAdd(command.line(), "Problem pay in group IO", e.getStackTrace(), exceptions);
            throw new RuntimeException("Server problem pay in group", e);
        }
    }

}
