package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PayGroupNotifications implements PayGroupNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int GROUP_PAYMENT = 3;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public PayGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    public void appendToGroupPayment(Command command, String friend) {
        try {
            if (HelpersNotifications.isSectionExistNotification(friend, notificationsDirectory)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], friend, command.args()[GROUP_PAYMENT],
                    notificationsDirectory);
            } else {
                appendAtPositionPayment(command, friend, notificationsDirectory);
            }
            if (HelpersNotifications.isSectionExistNotification(friend, tempNotif)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], friend, command.args()[GROUP_PAYMENT],
                    tempNotif);
            } else {
                appendAtPositionPayment(command, friend, tempNotif);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionPayment(Command command, String friend, ReaderWriterCreator creator)
        throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean reachedGroups = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].strip().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(friend)) {
                    reachedSection = true;
                    lines.add(line);
                } else if (reachedSection && line.strip().equals("Groups:")) {
                    reachedGroups = true;
                    reachedSection = false;
                    lines.add(line);
                } else if (reachedGroups) {
                    appendNewInfoPayment(lines, line, command);
                    reachedGroups = false;
                } else {
                    lines.add(line);
                }
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private void appendNewInfoPayment(List<String> lines, String line, Command command) {

        if (line.strip().equals("No information!")) {
            lines.add(String.format("*%s - %s approved your payment %.2f LV.", command.args()[GROUP_PAYMENT],
                command.line(), Double.parseDouble(command.args()[AMOUNT])));
        } else {

            lines.add(String.format("*%s - %s approved your payment %.2f LV.", command.args()[GROUP_PAYMENT],
                command.line(), Double.parseDouble(command.args()[AMOUNT])));
            lines.add(line);
        }
    }

    private void appendAtEnd(String name, String amount, String friend, String groupName, ReaderWriterCreator creator)
        throws IOException {

        String build = String.format("name:%s\n", friend) +
            String.format("Groups:\n*%s - %s approved your payment %.2f LV.", groupName, name,
                Double.parseDouble(amount));
        Helpers.appendToFile(build, creator);
    }
}
