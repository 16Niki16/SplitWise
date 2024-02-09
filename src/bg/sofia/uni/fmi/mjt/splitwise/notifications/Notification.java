package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class Notification implements NotificationAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int FRIEND_PAY = 2;
    private static final int REASON = 3;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public Notification(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendPayment(Command command) {
        try {
            if (!HelpersNotifications.checkSectionAlreadyExist(command.args()[FRIEND_PAY], notificationsDirectory)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], true, " ",
                    notificationsDirectory);
            } else {
                appendAtPositionPayment(command, notificationsDirectory);
            }
            if (!HelpersNotifications.checkSectionAlreadyExist(command.args()[FRIEND_PAY], tempNotif)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], true, " ",
                    tempNotif);
            } else {
                appendAtPositionPayment(command, tempNotif);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addNotificationFriendSplit(Command command) {
        try {
            if (!HelpersNotifications.checkSectionAlreadyExist(command.args()[FRIEND_PAY], notificationsDirectory)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], false,
                    Helpers.getReason(command), notificationsDirectory);
            } else {
                appendAtPositionSplit(command, notificationsDirectory);
            }
            if (!HelpersNotifications.checkSectionAlreadyExist(command.args()[FRIEND_PAY], tempNotif)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], false,
                    Helpers.getReason(command), tempNotif);
            } else {
                appendAtPositionSplit(command, tempNotif);
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

    private void appendAtPositionSplit(Command command, ReaderWriterCreator creator) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(command.args()[FRIEND_PAY])) {
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

    private void appendInListPaid(List<String> lines, String line, Command command) {
        if (line.trim().equals("Friends:")) {
            lines.add(line);
            lines.add(
                String.format("%s approved your payment %s LV.", command.line(), command.args()[AMOUNT]));
        } else {
            lines.add("Friends:");
            lines.add(
                String.format("%s approved your payment %s LV.", command.line(), command.args()[AMOUNT]));
            lines.add(line);
        }
    }

    private void appendInList(List<String> lines, String line, Command command) {
        double am = Double.parseDouble(command.args()[AMOUNT]) / TWO;
        if (!line.trim().equals("Friends:")) {
            lines.add("Friends:");
            lines.add(
                String.format(String.format("You owes %s %.2f LV[%s]", command.line(), am,
                    Helpers.getReason(command))));
            lines.add(line);
        } else {
            lines.add(line);
            lines.add(
                String.format(String.format("You owes %s %.2f LV[%s]", command.line(), am,
                    Helpers.getReason(command))));
        }
    }

    private void appendAtEnd(String name, String amount, String friend, boolean paid, String reason,
                             ReaderWriterCreator creator)
        throws IOException {
        try (BufferedWriter wr = new BufferedWriter(creator.getAppend())) {
            StringBuilder build = new StringBuilder(String.format("name: %s\n", friend));
            if (paid) {
                build.append(
                    String.format("Friends:\n%s approved your payment %s LV.\nGroups:\nNo information!", name, amount));
            } else {
                double am = Double.parseDouble(amount) / TWO;
                build.append(
                    String.format("Friends:\nYou owes %s %.2f[%s].\nGroups:\nNo information!", name, am, reason));
            }
            wr.write(String.valueOf(build));
            wr.newLine();
            wr.flush();
        }
    }
}
