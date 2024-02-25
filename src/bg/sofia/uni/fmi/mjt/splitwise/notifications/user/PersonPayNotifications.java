package bg.sofia.uni.fmi.mjt.splitwise.notifications.user;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PersonPayNotifications implements PersonPayNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    static final int FRIEND_PAY = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public PersonPayNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendPayment(Command command) {
        try {
            if (HelpersNotifications.isSectionExistNotification(command.args()[FRIEND_PAY], notificationsDirectory)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY],
                    notificationsDirectory);
            } else {
                appendAtPositionPayment(command, notificationsDirectory);
            }
            if (HelpersNotifications.isSectionExistNotification(command.args()[FRIEND_PAY], tempNotif)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY],
                    tempNotif);
            } else {
                appendAtPositionPayment(command, tempNotif);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void appendAtPositionPayment(Command command, ReaderWriterCreator creator) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;

            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(command.args()[FRIEND_PAY])) {
                    reachedSection = true;
                    lines.add(line);

                } else if (reachedSection) {
                    appendInListPaid(lines, line, command);
                    reachedSection = false;
                } else {
                    lines.add(line);
                }
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private void appendInListPaid(List<String> lines, String line, Command command) {
        if (line.strip().equals("Friends:")) {
            lines.add(line);
            lines.add(String.format("%s approved your payment %.2f LV.", command.line(),
                Double.parseDouble(command.args()[AMOUNT])));
        } else {
            lines.add("Friends:");
            lines.add(
                String.format("%s approved your payment %.2f LV.", command.line(),
                    Double.parseDouble(command.args()[AMOUNT])));
            lines.add(line);
        }
    }

    private void appendAtEnd(String name, String amount, String friend, ReaderWriterCreator creator)
        throws IOException {
        String build = String.format("name: %s\n", friend) +
            String.format("Friends:\n%s approved your payment %s LV.\nGroups:\nNo information!", name, amount);
        Helpers.appendToFile(build, creator);
    }
}
