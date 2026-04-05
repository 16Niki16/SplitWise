package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class GroupSplitCommand implements Command {

    public GroupSplitCommand() {

    }

    @Override
    public String execute(String... args) {
        try {
            double amount = Double.parseDouble(args[AMOUNT]);
            Group updateGroup = Group.ofSplit(Helpers.findGroupLine(command, groupsDirectory, GROUP_INDEX));

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

    @Override
    public String execute(User user) {
        return null;
    }
}
