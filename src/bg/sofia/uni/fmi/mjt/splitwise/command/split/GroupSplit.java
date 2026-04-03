package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.group.GroupAPI;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class GroupSplit implements GroupSplitAPI {
    private static final int GROUP_INDEX = 2;
    private static final int AMOUNT = 1;
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator notifications;
    private final ReaderWriterCreator tempNotif;
    private final User user;
    private final ExchangeRate rate;

    public GroupSplit(ReaderWriterCreator groupsDirectory, ReaderWriterCreator notifications,
                      ReaderWriterCreator tempNotif, User user, ExchangeRate rate) {
        this.groupsDirectory = groupsDirectory;
        this.notifications = notifications;
        this.tempNotif = tempNotif;
        this.user = user;
        this.rate = rate;
    }

    @Override
    public String groupsOwe(CommandLine command) {

        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);
            GroupAPI updateGroup = Group.ofSplit(Helpers.findGroupLine(command, groupsDirectory, GROUP_INDEX));

            if (!user.getCurrency().equalsIgnoreCase("bgn")) {
                amount = user.amountToAdd(rate.exchange(user.getCurrency(), "bgn"), amount, false);
            }

            String payment = updateGroup.addInformation(command, notifications, tempNotif, amount);

            Helpers.addInformation(Helpers.updatedGroup(command.args()[GROUP_INDEX], groupsDirectory, payment),
                groupsDirectory);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Group not found.", e);
        }
        return "Information successfully added";
    }
}
