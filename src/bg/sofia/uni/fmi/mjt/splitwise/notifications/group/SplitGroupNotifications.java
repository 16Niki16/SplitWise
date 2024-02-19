package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class SplitGroupNotifications implements SplitGroupNotificationAPI {

    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int GROUP_NAME = 2;
    private static final int GROUP_PAYMENT = 3;
    private ReaderWriterCreator notificationsDirectory;
    private ReaderWriterCreator tempNotif;

    public SplitGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void appendToGroupSplit(Command command, String friend, String amount) {
        try {
            if (HelpersNotifications.isSectionExistNotification(friend, notificationsDirectory)) {
                appendAtEnd(command.line(), amount, friend, Helpers.getReason(command),
                    command.args()[GROUP_NAME], notificationsDirectory);
            } else {
                appendAtPositionSplit(command, friend, amount, notificationsDirectory);
            }
            if (HelpersNotifications.isSectionExistNotification(friend, tempNotif)) {
                appendAtEnd(command.line(), amount, friend, Helpers.getReason(command),
                    command.args()[GROUP_NAME], tempNotif);
            } else {
                appendAtPositionSplit(command, friend, amount, tempNotif);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionSplit(Command command, String friend, String amount, ReaderWriterCreator creator)
        throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean reachedGroups = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(friend)) {
                    reachedSection = true;
                    lines.add(line);
                } else if (reachedSection && line.trim().equals("Groups:")) {
                    reachedGroups = true;
                    reachedSection = false;
                    lines.add(line);
                } else if (reachedGroups) {
                    appendNewInfoSplit(lines, line, command, amount);
                    reachedGroups = false;
                } else {
                    lines.add(line);
                }
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private void appendNewInfoSplit(List<String> lines, String line, Command command, String amount) {
        if (line.trim().equals("No information!")) {
            lines.add(
                String.format("*%s - You owes %s %.2f LV[%s]", command.args()[TWO], command.line(),
                    Double.parseDouble(amount), Helpers.getReason(command)));
        } else {
            lines.add(
                String.format("*%s - You owes %s %2f LV[%s]", command.args()[TWO], command.line(),
                    Double.parseDouble(amount), Helpers.getReason(command)));
            lines.add(line);
        }
    }

    private void appendAtEnd(String name, String amount, String friend, String reason, String groupName,
                             ReaderWriterCreator creator)
        throws IOException {
        StringBuilder build = new StringBuilder(String.format("name:%s\n", friend));
        build.append(String.format("Groups:\n*%s - You owes %s %.2f LV[%s]", groupName, name,
            Double.parseDouble(amount), reason.strip()));
        Helpers.appendToFile(String.valueOf(build), creator);
    }
}
