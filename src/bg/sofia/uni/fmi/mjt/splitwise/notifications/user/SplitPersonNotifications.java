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
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class SplitPersonNotifications implements SplitPersonNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    static final int FRIEND_PAY = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public SplitPersonNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendSplit(Command command) {
        try {
            if (HelpersNotifications.isSectionExistNotification(command.args()[FRIEND_PAY], notificationsDirectory)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY],
                    Helpers.getReason(command), notificationsDirectory);
            } else {
                appendAtPositionSplit(command, notificationsDirectory);
            }
            if (HelpersNotifications.isSectionExistNotification(command.args()[FRIEND_PAY], tempNotif)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY],
                    Helpers.getReason(command), tempNotif);
            } else {
                appendAtPositionSplit(command, tempNotif);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionSplit(Command command, ReaderWriterCreator creator) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].equals("name") &&
                    checkName[FRIEND_NAME].equals(command.args()[FRIEND_PAY])) {
                    lines.add(line);
                    reachedSection = true;
                } else if (reachedSection) {
                    appendInList(lines, line, command);
                    reachedSection = false;
                } else {
                    lines.add(line);
                }
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private void appendInList(List<String> lines, String line, Command command) {
        double am = Double.parseDouble(command.args()[AMOUNT]) / TWO;
        if (!line.trim().equals("Friends:")) {
            lines.add("Friends:");
            lines.add(
                String.format(String.format("You owe %s %.2f LV[%s]", command.line(), am,
                    Helpers.getReason(command))));
            lines.add(line);
        } else {
            lines.add(line);
            lines.add(
                String.format(String.format("You owe %s %.2f LV[%s]", command.line(), am,
                    Helpers.getReason(command))));
        }
    }

    private void appendAtEnd(String name, String amount, String friend, String reason,
                             ReaderWriterCreator creator)
        throws IOException {
        StringBuilder build = new StringBuilder(String.format("name:%s\n", friend));
        double am = Double.parseDouble(amount) / TWO;
        build.append(
            String.format("Friends:\nYou owe %s %.2f[%s].\nGroups:\nNo information!", name, am, reason));
        Helpers.appendToFile(String.valueOf(build), creator);
    }
}
